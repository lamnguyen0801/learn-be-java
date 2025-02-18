package work.vietdefi.clean.services;

// Import necessary interfaces and classes for user services and database interactions
import work.vietdefi.clean.services.user.IUserService;
import work.vietdefi.clean.services.user.UserService;
import work.vietdefi.sql.HikariClient;
import work.vietdefi.sql.SqlJavaBridge;

import java.io.IOException;

/**
 * The SharedServices class is responsible for initializing and managing
 * shared service instances within the application. It sets up the necessary
 * components to enable interaction with the user-related data stored in
 * a SQL database.
 *
 * <p>This class is designed to be used as a singleton, providing a centralized
 * access point for service instances such as IUserService. It simplifies the
 * configuration and instantiation of services, ensuring that they can be easily
 * accessed throughout the application lifecycle.</p>
 */
public class SharedServices {
    //Table name to store user data in sql database
    public static final String USER_TABLE = "user";
    // Static instance of HikariClient that provides sql-related services throughout the application
    public static HikariClient hikariClient;
    // Static instance of SqlJavaBridge that provides sql-related services throughout the application
    public static SqlJavaBridge sqlJavaBridge;
    // Static instance of IUserService that provides user-related services throughout the application
    public static IUserService userService;

    /**
     * Initializes the shared services by configuring the database connection
     * and creating an instance of the user service.
     *
     * <p>This method sets up the necessary components to allow the application to
     * interact with the user-related data stored in the database.</p>
     *
     * @throws IOException if there is an issue reading the database configuration file,
     *         such as the file not being found or being malformed.
     */
    public static void init() throws IOException {
        // Create a HikariClient instance to manage database connections
        // It reads configuration settings from the specified JSON file.
        hikariClient = new HikariClient("config/sql/databases.json");

        // Create an instance of SqlJavaBridge using the HikariClient.
        // This bridge facilitates communication between Java applications and the SQL database,
        // allowing for execution of SQL queries and transaction management.
        sqlJavaBridge = new SqlJavaBridge(hikariClient);

        // Instantiate the user service with the SQL bridge and specify the "user" table
        // This user service will handle operations related to user data.
        userService = new UserService(sqlJavaBridge, USER_TABLE);
    }
}
