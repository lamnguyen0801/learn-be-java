package work.vietdefi.sql;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * ISQLJavaBridge defines an interface for interacting with SQL databases.
 * It provides methods for checking tables, creating them, performing queries,
 * converting query results to JSON, and executing inserts/updates.
 */
public interface ISQLJavaBridge {


    /**
     * Checks if a specified table exists in the database.
     *
     * @param table The name of the table to check.
     * @return true if the table exists, false otherwise.
     */
    boolean checkTableExisting(String table);


    /**
     * Creates a new table and optionally creates indexes for it.
     *
     * @param createTableSQL The SQL statement to create the table.
     * @param createIndexSql Optional SQL statements to create indexes on the table.
     * @return true if the table and indexes are created successfully, false otherwise.
     */
    boolean createTable(String createTableSQL, String... createIndexSql);


    /**
     * Converts a single row from the ResultSet to a JsonObject.
     *
     * @param resultSet The ResultSet containing the query result.
     * @return A JsonObject representing the row data.
     * @throws SQLException If an error occurs while accessing the ResultSet.
     */
    JsonObject convertResultSetToJsonObject(ResultSet resultSet) throws Exception;


    /**
     * Converts the entire ResultSet to a JsonArray, where each row is represented as a JsonObject.
     *
     * @param resultSet The ResultSet containing multiple rows of query results.
     * @return A JsonArray representing the query result.
     * @throws SQLException If an error occurs while accessing the ResultSet.
     */
    JsonArray convertResultSetToJsonArray(ResultSet resultSet) throws Exception;


    /**
     * Executes a query that is expected to return a single row and converts the result to a JsonObject.
     *
     * @param query The SQL query to execute.
     * @param params The parameters to bind to the query, if any.
     * @return A JsonObject representing the single row result.
     * @throws Exception If the query execution or conversion fails.
     */
    JsonObject queryOne(String query, Object... params) throws Exception;


    /**
     * Executes a query that returns multiple rows and converts the result to a JsonArray.
     *
     * @param query The SQL query to execute.
     * @param params The parameters to bind to the query, if any.
     * @return A JsonArray containing all rows of the query result.
     * @throws Exception If the query execution or conversion fails.
     */
    JsonArray queryArray(String query, Object... params) throws Exception;


    /**
     * Executes an INSERT statement and returns the generated key.
     *
     * @param query The SQL INSERT query to execute.
     * @param params The parameters to bind to the query, if any.
     * @return The generated key from the insert operation (e.g., auto-increment ID).
     * @throws Exception If the insertion or key retrieval fails.
     */
    Object insert(String query, Object... params) throws Exception;


    /**
     * Executes an UPDATE or DELETE statement and returns the number of rows affected.
     *
     * @param query The SQL UPDATE/DELETE query to execute.
     * @param params The parameters to bind to the query, if any.
     * @return The number of rows affected by the operation.
     * @throws Exception If the query execution fails.
     */
    int update(String query, Object... params) throws Exception;


    /**
     * Closes any resources held by the SQL bridge.
     * <p>
     * This method should be called to release any database connections,
     * statements, or other resources that are no longer needed.
     * </p>
     */
    void close();
}
