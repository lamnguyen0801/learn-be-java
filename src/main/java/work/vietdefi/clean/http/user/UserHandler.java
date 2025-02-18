package work.vietdefi.clean.http.user;

// Import necessary classes for handling HTTP requests and responses
import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import work.vietdefi.clean.services.SharedServices;
import work.vietdefi.clean.services.common.SimpleResponse;
import work.vietdefi.util.json.GsonUtil;
import work.vietdefi.util.log.DebugLogger;

/**
 * The UserHandler class is responsible for handling HTTP requests related
 * to user operations, such as authorization, registration, login, and
 * fetching user data. It provides methods to process requests and send
 * appropriate responses back to the client.
 *
 * <p>This class interacts with shared services to perform user-related
 * actions and communicates the results back to the client through HTTP
 * responses.</p>
 *
 * <p>The class provides the following functionalities:</p>
 * <ul>
 *   <li>Authorization of users based on provided tokens.</li>
 *   <li>Registration of new users (method currently not implemented).</li>
 *   <li>Login of existing users (method currently not implemented).</li>
 *   <li>Fetching user data (method currently not implemented).</li>
 * </ul>
 */
public class UserHandler {

    /**
     * Handles user authorization requests.
     *
     * <p>This method retrieves a token from the request header and calls
     * the authorization service to validate the user. If successful, it
     * adds user-related information to the response headers and passes
     * control to the next handler in the chain. If the authorization fails,
     * it responds with an unauthorized error.</p>
     *
     * @param routingContext the context of the current HTTP request,
     *        which contains request and response objects.
     */
    public static void authorize(RoutingContext routingContext) {
        try {
            // Retrieve the token from the request header
            String token = routingContext.request().getHeader("token");

            // Call the authorization service to validate the token
            JsonObject response = SharedServices.userService.authorization(token);

            // Check if the authorization was successful
            if (SimpleResponse.isSuccess(response)) {
                // Retrieve user information from the response
                JsonObject user = response.getAsJsonObject("d");
                String user_id = user.get("user_id").getAsString();
                String username = user.get("username").getAsString();

                // Add user information to the request headers for later use
                routingContext.request().headers().add("user_id", user_id);
                routingContext.request().headers().add("username", username);

                // Pass control to the next handler
                routingContext.next();
            } else {
                // Unauthorized request - respond with error code
                routingContext.response().end(SimpleResponse.createResponse(2).toString());
            }
        } catch (Exception e) {
            // Log the error and respond with a failure message
            DebugLogger.logger.error("", e);
            routingContext.response().end(SimpleResponse.createResponse(1).toString());
        }
    }

    /**
     * Handles user registration requests.
     *
     * <p>This method is currently not implemented but is intended to process
     * user registration data and communicate the result back to the client.</p>
     *
     * @param routingContext the context of the current HTTP request.
     */
    public static void register(RoutingContext routingContext) {
        try {
            String body = routingContext.body().asString();

            JsonObject bodyJson = GsonUtil.gsonConverter.toJsonElement(body).getAsJsonObject();
            String username = bodyJson.get("username").getAsString();
            String password = bodyJson.get("password").getAsString();

            JsonObject response = SharedServices.userService.register(username, password);

            if(SimpleResponse.isSuccess(response)) {
                routingContext.response().end(response.toString());
            } else {
                routingContext.response().end(SimpleResponse.createResponse(10).toString());
            }
        } catch (Exception e) {
            // Log the error and respond with a failure message
            DebugLogger.logger.error("", e);
            routingContext.response().end(SimpleResponse.createResponse(1).toString());
        }
    }

    /**
     * Handles user login requests.
     *
     * <p>This method is currently not implemented but is intended to process
     * user login data and respond to the client accordingly.</p>
     *
     * @param routingContext the context of the current HTTP request.
     */
    public static void login(RoutingContext routingContext) {
        try {
            String body = routingContext.body().asString();

            JsonObject bodyJson = GsonUtil.gsonConverter.toJsonElement(body).getAsJsonObject();
            String username = bodyJson.get("username").getAsString();
            String password = bodyJson.get("password").getAsString();

            JsonObject response = SharedServices.userService.login(username, password);

            if(SimpleResponse.isSuccess(response)) {
                routingContext.response().end(response.toString());
            } else {
                int error = response.get("e").getAsInt();
                routingContext.response().end(SimpleResponse.createResponse(error).toString());
            }
        } catch (Exception e) {
            // Log the error and respond with a failure message
            DebugLogger.logger.error("", e);
            routingContext.response().end(SimpleResponse.createResponse(1).toString());
        }
    }

    /**
     * Handles requests to fetch user data.
     *
     * <p>This method is currently not implemented but is intended to retrieve
     * user information and respond to the client with the data.</p>
     *
     * @param routingContext the context of the current HTTP request.
     */
    public static void get(RoutingContext routingContext) {
        try {
            // Retrieve user_id from the header. After passing authorization, the token is considered valid.
            // We add user_id and username to the header so that they can be used in later handlers.

            long user_id = Long.parseLong(routingContext
                    .request().getHeader("user_id"));

            JsonObject response = SharedServices.userService.get(user_id);
            if(SimpleResponse.isSuccess(response)) {
                routingContext.response().end(response.toString());
            }

        } catch (Exception e) {
            // Log the error and respond with a failure message
            DebugLogger.logger.error("", e);
            routingContext.response().end(SimpleResponse.createResponse(1).toString());
        }
    }
}
