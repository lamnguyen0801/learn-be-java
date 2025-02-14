package work.vietdefi.clean.services.user;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;
import work.vietdefi.clean.services.common.SimpleResponse;
import work.vietdefi.sql.HikariClient;
import work.vietdefi.sql.ISQLJavaBridge;
import work.vietdefi.sql.SqlJavaBridge;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserService.
 * Tests are run against the 'test_user' table, which is dropped after tests complete.
 */
public class UserServiceTest {

    private static ISQLJavaBridge bridge;
    private static UserService userService;
    private static final String TEST_TABLE = "test_user";
    private static final String USERNAME = "test_username";
    private static final String PASSWORD = "password123";

    /**
     * Set up resources before all tests.
     * Initialize the SQL bridge and create an instance of UserService.
     */
    @BeforeAll
    static void setup() throws IOException {
        HikariClient hikariClient = new HikariClient("config/sql/databases.json");
        bridge = new SqlJavaBridge(hikariClient); // Create an instance of SqlJavaBridge
        // Initialize the UserService with the test table.
        userService = new UserService(bridge, TEST_TABLE);
    }

    /**
     * Clean up resources after all tests.
     * Drop the 'test_user' table to ensure a clean slate.
     */
    @AfterAll
    static void teardown() throws Exception {
        bridge.update("DROP TABLE IF EXISTS " + TEST_TABLE);
    }

    /**
     * Delete data in table 'test_user' before each testc
     */
    @BeforeEach
    void cleanDatabase() throws Exception {
        bridge.update("DELETE FROM " + TEST_TABLE);
    }

    /**
     * Test user registration.
     * Verify that a user can be successfully registered.
     */
    @Test
    void testRegisterUser() {
        JsonObject response = userService.register(USERNAME, PASSWORD);

        assertNotNull(response);
        assertTrue(SimpleResponse.isSuccess(response));
        JsonObject data = response.getAsJsonObject("d");
        assertEquals(USERNAME, data.get("username").getAsString());
        assertNotNull(data.get("token").getAsString());
    }

    /**
     * Test if username already exists.
     * Verify that registering an existing user returns error code 10.
     */
    @Test
    void testRegisterExistUsername() {
        userService.register(USERNAME, PASSWORD); // register first time
        JsonObject response = userService.register(USERNAME, PASSWORD); // register second time

        assertNotNull(response);
        assertEquals(10, response.get("e").getAsInt());
    }

    /**
     * Test user login.
     * Verify that a user with valid credentials can log in.
     */
    @Test
    void testLoginUser() {
        userService.register(USERNAME, PASSWORD);

        JsonObject response = userService.login(USERNAME, PASSWORD);

        assertNotNull(response);
        assertTrue(SimpleResponse.isSuccess(response));
        JsonObject data = response.getAsJsonObject("d");
        assertEquals(USERNAME, data.get("username").getAsString());
        assertNotNull(data.get("token").getAsString());
    }

    /**
     * Test if username is not exist
     * Verify that user can not log in with a non-existent username
     * return error code 10
     */
    @Test
    void testLoginNotExistUsername() {
        JsonObject response = userService.login(USERNAME, PASSWORD);

        assertNotNull(response);
        assertEquals(10, response.get("e").getAsInt());
    }

    /**
     * Test if username is not exist
     * Verify that user can not log in with a non-existent username
     * return error code 11
     */
    @Test
    void testLoginWrongPassword() {
        String incorrectPassword = "password";

        userService.register(USERNAME, PASSWORD);
        JsonObject response = userService.login(USERNAME, incorrectPassword);

        assertNotNull(response);
        assertEquals(11, response.get("e").getAsInt());
    }

    /**
     * Test token authorization.
     * Verify that a valid token allows authorization.
     */
    @Test
    void testAuthorization() {
        userService.register(USERNAME, PASSWORD);
        JsonObject loginResponse = userService.login(USERNAME, PASSWORD);

        assertNotNull(loginResponse);
        String token = loginResponse.getAsJsonObject("d").get("token").getAsString();

        JsonObject authResponse = userService.authorization(token);
        assertTrue(SimpleResponse.isSuccess(authResponse));

        JsonObject fakeResponse = userService.authorization("fake_token");
        assertFalse(SimpleResponse.isSuccess(fakeResponse));
    }

    /**
     * Test authorization with incorrect token.
     * Verify that authorize using an incorrect token
     * return error code 10
     */
    @Test
    void testIncorrectToken() {
        userService.register(USERNAME, PASSWORD);
        userService.login(USERNAME, PASSWORD);

        String incorrectToken = "123";
        JsonObject authResponse = userService.authorization(incorrectToken);

        assertEquals(10, authResponse.get("e").getAsInt());
    }

    /**
     * Test authorization with expired token.
     * Verify that authorization with incorrect token
     * return error code 10
     */

    @Test
    void testExpiredToken() throws Exception {
        userService.register(USERNAME, PASSWORD);
        JsonObject loginResponse = userService.login(USERNAME, PASSWORD);
        String token = loginResponse.getAsJsonObject("d").get("token").getAsString();

        String sql = "UPDATE " + TEST_TABLE + " SET token_expired = ? WHERE username = ?";
        bridge.update(sql, System.currentTimeMillis() - 1000,USERNAME);

        JsonObject authResponse = userService.authorization(token);
        assertEquals(11, authResponse.get("e").getAsInt());
    }
}
