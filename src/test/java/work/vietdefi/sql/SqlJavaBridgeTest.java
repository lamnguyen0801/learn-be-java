package work.vietdefi.sql;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;


class SqlJavaBridgeTest {
    // Static instance of SqlJavaBridge to be used in the tests
    private static SqlJavaBridge sqlJavaBridge;


    /**
     * This method is run once before all tests in this class.
     * It sets up the environment by initializing the HikariClient and SqlJavaBridge instances.
     * It also drops any existing tables to ensure a clean test environment.
     *
     * @throws Exception If an error occurs during setup.
     */
    @BeforeAll
    static void setUp() throws Exception {
        // Initialize HikariClient with configuration file for database connections
        HikariClient hikariClient = new HikariClient("config/sql/databases.json");
        sqlJavaBridge = new SqlJavaBridge(hikariClient); // Create an instance of SqlJavaBridge
        // Drop test tables if they already exist to avoid conflicts
        sqlJavaBridge.update("DROP TABLE IF EXISTS test_table");
        sqlJavaBridge.update("DROP TABLE IF EXISTS test_users");
    }


    /**
     * Test to check the existence of a table and create it if it does not exist.
     * This method verifies that the table is initially absent, creates it,
     * and then confirms its existence.
     */
    @Test
    void testCheckTableExistsAndCreateTable(){
        String tableName = "test_table"; // Name of the table to be tested


        // Check that the table does not exist initially
        assertFalse(sqlJavaBridge.checkTableExisting(tableName), "Table should not exist initially");


        // SQL statement to create the test_table
        String createTableSQL = "CREATE TABLE test_table (id INT PRIMARY KEY, name VARCHAR(255))";
        // Create the table and check that it was created successfully
        assertTrue(sqlJavaBridge.createTable(createTableSQL), "Table should be created successfully");


        // Verify that the table now exists
        assertTrue(sqlJavaBridge.checkTableExisting(tableName), "Table should exist after creation");
    }


    /**
     * Test to perform various SQL operations such as creating a table,
     * inserting records, and querying them.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    void testSQLOperations() throws Exception {
        // SQL statement to create the test_users table
        String createTableSQL = "CREATE TABLE test_users(id INT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(255))";
        // Create the test_users table and verify its successful creation
        assertTrue(sqlJavaBridge.createTable(createTableSQL), "test_users table should be created successfully");


        // SQL statement to insert the first user into test_users
        String insertSQL1 = "INSERT INTO test_users (username) VALUES (?)";
        Object generatedKey1 = sqlJavaBridge.insert(insertSQL1, "testuser1"); // Insert user and capture the generated key
        assertNotNull(generatedKey1, "Generated key should not be null after insertion"); // Verify that the key is generated


        // SQL statement to query the inserted user by their generated ID
        String querySQL = "SELECT * FROM test_users WHERE id = ?";
        JsonObject user = sqlJavaBridge.queryOne(querySQL, generatedKey1); // Retrieve the user
        assertNotNull(user, "User should be retrieved successfully"); // Check that the user is retrieved
        // Verify that the retrieved username matches the inserted value
        assertEquals("testuser1", user.get("username").getAsString(), "Username should match the inserted value");


        // SQL statement to insert the second user into test_users
        String insertSQL2 = "INSERT INTO test_users (username) VALUES (?)";
        Object generatedKey2 = sqlJavaBridge.insert(insertSQL2, "testuser2"); // Insert second user
        assertNotNull(generatedKey2, "Generated key should not be null after insertion"); // Check that the key is generated


        // SQL statement to query the inserted user by their generated ID
        String updateSQL = "UPDATE test_users SET username = ? WHERE id = ?";
        int row = sqlJavaBridge.update(updateSQL, "testuser2_updated", generatedKey2); // Retrieve the user
        assertEquals(1, row, "There should be one row affected after update");


        // SQL statement to query all users from the test_users table
        querySQL = "SELECT * FROM test_users";
        JsonArray userArray = sqlJavaBridge.queryArray(querySQL); // Retrieve all users
        assertNotNull(userArray, "User array should not be null"); // Ensure the user array is retrieved
        // Verify that two users are present in the array
        assertEquals(2, userArray.size(), "There should be two users in the test_users table");
    }


    /**
     * This method is run once after all tests in this class.
     * It cleans up the test environment by dropping the test tables
     * and closing the database connection.
     *
     * @throws Exception If an error occurs during teardown.
     */
    @AfterAll
    static void tearDown() throws Exception {
        // Drop the test tables to clean up after the tests
        sqlJavaBridge.update("DROP TABLE IF EXISTS test_table");
        sqlJavaBridge.update("DROP TABLE IF EXISTS test_users");
        sqlJavaBridge.close(); // Close the database connection
    }
}
