package work.vietdefi.clean.services.user;

import com.google.gson.JsonObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import work.vietdefi.clean.services.common.SimpleResponse;
import work.vietdefi.sql.ISQLJavaBridge;

/**
 * UserService is an implementation of the IUserService interface.
 * It manages user operations such as registration, login, and authorization,
 * and interacts with the database via the ISQLJavaBridge.
 */
public class UserService implements IUserService {

    // A bridge to interact with the underlying SQL database.
    private ISQLJavaBridge bridge;

    // The name of the table where user data is stored.
    private String table;

    /**
     * Constructs a new UserService with the given SQL bridge and table name.
     * If the specified table does not exist, it is automatically created.
     *
     * @param bridge The ISQLJavaBridge used to interact with the database.
     * @param table  The name of the user table in the database.
     */
    public UserService(ISQLJavaBridge bridge, String table) {
        this.bridge = bridge;
        this.table = table;


        // Check if the table exists, and create it if necessary.
        if (!bridge.checkTableExisting(table)) {
            createTable();
        }
    }

    /**
     * Creates the user table with the appropriate schema and indexes.
     * The schema includes columns for user ID, username, password, token,
     * and token expiration time.
     */
    private void createTable() {
        String createTableSQL = ("CREATE TABLE IF NOT EXISTS table_name ("
                + "user_id BIGINT PRIMARY KEY AUTO_INCREMENT," // Unique ID for each user.
                + "username VARCHAR(64) UNIQUE,"               // Username, must be unique.
                + "password VARCHAR(2048),"                    // Encrypted password.
                + "token VARCHAR(2048),"                       // Authentication token.
                + "token_expired BIGINT DEFAULT 0"             // Expiration timestamp for the token.
                + ")").replace("table_name", table);

        // SQL to create a unique index on the username column.
        String indexSQL1 = "CREATE UNIQUE INDEX table_name_username_uindex ON table_name (username)"
                .replace("table_name", table);

        // SQL to create an index on the token column.
        String indexSQL2 = "CREATE INDEX table_name_token_index ON table_name (token)"
                .replace("table_name", table);

        // Execute the SQL statements to create the table and indexes.
        bridge.createTable(createTableSQL, indexSQL1, indexSQL2);
    }

    /**
     * Registers a new user with the provided username and password.
     *
     * @param username The username for the new user.
     * @param password The password for the new user (should be encrypted before storage).
     * @return A JsonObject indicating the result of the registration (e.g., success or error).
     */
    @Override
    public JsonObject register(String username, String password) {
        try {
            // check exist username
            String checkSql = "SELECT user_id FROM table_name WHERE username = ?".replace("table_name", table);
            JsonObject checkResult = bridge.queryOne(checkSql, username);
            if(checkResult != null) {
                return SimpleResponse.createResponse(10);
            }

            // insert user
            String hashedPassword = DigestUtils.sha512Hex(password);
            String insertSql = "INSERT INTO " + table + " (username, password) VALUES (?, ?)";
            Object insertResult = bridge.insert(insertSql, username, hashedPassword);
            if (insertResult != null) {
                JsonObject userData = new JsonObject();
                userData.addProperty("username", username);

                // generate random token and exprire after 24h
                String token = RandomStringUtils.randomAlphanumeric(8);
                long tokenExpiration = System.currentTimeMillis() + (24 * 60 * 60 * 1000);

                // save and update token into db
                String updateSql = "UPDATE table_name SET token = ?, token_expired = ? WHERE user_id = ?".replace("table_name", table);
                long userId = ((Number) insertResult).longValue();
                int updateResult = bridge.update(updateSql, token, tokenExpiration, userId);
                if(updateResult > 0) {
                    userData.addProperty("token", token);
                    userData.addProperty("token_expired", tokenExpiration);
                }
                return SimpleResponse.createResponse(0, userData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Logs in a user by validating the provided username and password.
     *
     * @param username The username of the user attempting to log in.
     * @param password The corresponding password for authentication.
     * @return A JsonObject containing the login result (e.g., token or error message).
     */
    @Override
    public JsonObject login(String username, String password) {
        try {
            // check username and password
            String hashedPassword = DigestUtils.sha512Hex(password);
            String checkSql = "SELECT user_id FROM table_name WHERE username = ? AND password = ?".replace("table_name", table);
            JsonObject checkResult = bridge.queryOne(checkSql, username, hashedPassword);
            if(checkResult == null) {
                String checkUsernameSql = "SELECT user_id FROM table_name WHERE username = ?".replace("table_name", table);
                JsonObject checkUsernameResult = bridge.queryOne(checkUsernameSql, username);
                if(checkUsernameResult == null) {
                    return SimpleResponse.createResponse(10); // user not exist
                } else {
                    return SimpleResponse.createResponse(11); // wrong password
                }
            }

            JsonObject dataUser = new JsonObject();
            dataUser.addProperty("username", username);

            // generate random token and exprire after 24h
            String token = RandomStringUtils.randomAlphanumeric(8);
            long tokenExpiration = System.currentTimeMillis() + (24 * 60 * 60 * 1000);

            // save and update token into db
            String updateSql = "UPDATE table_name SET token = ?, token_expired = ? WHERE user_id = ?".replace("table_name", table);
            long userId = checkResult.get("user_id").getAsLong();
            int updateResult = bridge.update(updateSql, token, tokenExpiration, userId);
            if(updateResult > 0) {
                dataUser.addProperty("token", token);
                dataUser.addProperty("token_expired", tokenExpiration);
            }

            return SimpleResponse.createResponse(0, dataUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Authorizes a user by validating the provided authentication token.
     *
     * @param token The token to be verified for authorization.
     * @return A JsonObject indicating whether the token is valid or expired, along with any relevant data.
     */
    @Override
    public JsonObject authorization(String token) {
        try {
            JsonObject dataUser = new JsonObject();

            String checkSql = "SELECT user_id, token_expired FROM table_name WHERE token = ?".replace("table_name", table);
            JsonObject checkResult = bridge.queryOne(checkSql, token);
            if(checkResult == null) {
                return SimpleResponse.createResponse(10); // wrong token
            } else {
                long currentTime = System.currentTimeMillis();
                long expiresAt = checkResult.get("token_expired").getAsLong();
                dataUser.addProperty("token_expired", expiresAt);
                if(currentTime > expiresAt) {
                    return SimpleResponse.createResponse(11); // token expires
                }
            }

            dataUser.addProperty("token", token);
            return SimpleResponse.createResponse(0, dataUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
