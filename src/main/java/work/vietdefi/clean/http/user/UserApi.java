package work.vietdefi.clean.http.user;

// Import necessary classes for handling HTTP routing
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import static work.vietdefi.clean.http.VertxHttpApi.createPath;

/**
 * The UserApi class configures the HTTP API endpoints related to user operations.
 * It defines both public and private APIs, mapping HTTP requests to their respective
 * handler methods in the UserHandler class.
 *
 * <p>This class is responsible for setting up routes for user login and registration,
 * as well as handling authorization for logged-in users.</p>
 *
 * <p>The class provides the following functionalities:</p>
 * <ul>
 *   <li>Configures public API endpoints for user registration and login.</li>
 *   <li>Configures private API endpoints for authorized user actions.</li>
 * </ul>
 */
public class UserApi {

    /**
     * Configures the API by setting up public and private routes.
     *
     * <p>This method is the entry point for setting up the user-related API
     * endpoints. It delegates the configuration to privateApi() for private routes
     * and publicApi() for public routes.</p>
     *
     * @param router the Vert.x router used to configure the API endpoints.
     */
    public static void configAPI(Router router) {
        publicApi(router);  // Configure public endpoints
        privateApi(router); // Configure private endpoints
    }

    /**
     * Configures the private API endpoints that require authorization.
     *
     * <p>This method sets up routes for authorized actions, specifically the
     * login functionality, which allows users to authorize their requests
     * and fetch user data.</p>
     *
     * @param router the Vert.x router used to configure the API endpoints.
     */
    private static void privateApi(Router router) {
        router.get(createPath("/user/get")) // Route for user login
                .handler(UserHandler::authorize) // Handle authorization
                .handler(UserHandler::get); // Fetch user data after authorization
    }

    /**
     * Configures the public API endpoints that do not require authorization.
     *
     * <p>This method sets up routes for user registration and login, allowing
     * new users to register and existing users to log in without prior authorization.</p>
     *
     * @param router the Vert.x router used to configure the API endpoints.
     */
    private static void publicApi(Router router) {
        router.post(createPath("/user/register")) // Route for user registration
                .handler(BodyHandler.create(false)) // Handle body of the request
                .handler(UserHandler::register); // Handle registration logic

        router.post(createPath("/user/login")) // Route for user login
                .handler(BodyHandler.create(false)) // Handle body of the request
                .handler(UserHandler::login); // Handle login logic
    }
}
