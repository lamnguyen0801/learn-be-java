package work.vietdefi.clean.services.user;

import com.google.gson.JsonObject;

/**
 * IUserService defines the contract for user-related operations.
 * It provides methods for registering, logging in, and authorizing users.
 */
public interface IUserService {

    /**
     * Registers a new user with the given username and password.
     *
     * @param username The username chosen by the user (must be unique).
     * @param password The password for the user account (should be encrypted before storage).
     * @return A JsonObject containing the registration result (e.g., success status, user ID, or error messages).
     */
    JsonObject register(String username, String password);

    /**
     * Authenticates a user by verifying their username and password.
     *
     * @param username The username of the user attempting to log in.
     * @param password The corresponding password provided for login.
     * @return A JsonObject containing the login result (e.g., success status, user token, or error messages).
     */
    JsonObject login(String username, String password);

    /**
     * Validates the provided token to ensure the user has the appropriate permissions.
     *
     * @param token The authentication token issued to the user after login.
     * @return A JsonObject indicating whether the token is valid and, if valid, any relevant user information or permissions.
     */
    JsonObject authorization(String token);
}
