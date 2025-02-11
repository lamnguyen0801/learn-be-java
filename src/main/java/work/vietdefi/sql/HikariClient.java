package work.vietdefi.sql;


import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import work.vietdefi.json.GsonConverter;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;


/**
 * HikariClient is a utility class that manages database connections using HikariCP,
 * a high-performance JDBC connection pool.
 */
public class HikariClient {
    public HikariDataSource dataSource;  // Manages the pool of database connections.


    /**
     * Constructor that initializes the HikariDataSource using configuration from a JSON file.
     *
     * @param configFilePath The path to the JSON configuration file.
     * @throws IOException If there is an issue reading the configuration files.
     */
    public HikariClient(String configFilePath) throws IOException {
        // Read the JSON config file content with UTF-8 encoding.
        String data = new String(Files.readAllBytes(Paths.get(configFilePath)), StandardCharsets.UTF_8);


        // Convert the JSON string to a JsonObject using Gson.
        GsonConverter converter = new GsonConverter();
        JsonObject json = converter.toJsonElement(data).getAsJsonObject();


        // Extract essential values from the JSON object.
        String jdbcUrl = json.get("jdbcUrl").getAsString();
        String user = json.get("dataSource.user").getAsString();
        String password = json.get("dataSource.password").getAsString();
        String database = json.get("dataSource.database").getAsString();
        String poolConfigFile = json.get("other.config.file").getAsString();


        // Load additional HikariCP properties from another config file.
        InputStream input = Files.newInputStream(Paths.get(poolConfigFile));
        Properties prop = new Properties();


        // Populate the Properties object with essential data source configurations.
        prop.put("jdbcUrl", jdbcUrl);
        prop.put("dataSource.user", user);
        prop.put("dataSource.password", password);
        prop.put("dataSource.database", database);


        // Load other properties from the external file.
        prop.load(input);


        // Create a HikariConfig using the populated properties.
        HikariConfig config = new HikariConfig(prop);


        // Initialize the HikariDataSource with the given configuration.
        dataSource = new HikariDataSource(config);
    }


    /**
     * Retrieves a database connection from the HikariCP pool.
     *
     * @return A Connection object for interacting with the database.
     * @throws SQLException If there is an issue acquiring a connection.
     */
    public Connection getConnection() throws SQLException {
        // Get a connection from the pool.
        return dataSource.getConnection();
    }


    /**
     * Closes the HikariDataSource and releases all resources.
     * <p>
     * This method should be called when the HikariClient is no longer needed
     * to ensure that all database connections are closed and resources
     * are released, preventing memory leaks.
     * </p>
     */
    public void close() {
        if (dataSource != null) {
            dataSource.close(); // Close the HikariCP data source
        }
    }
}

