package work.vietdefi.clean.http;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import work.vietdefi.clean.services.common.SimpleResponse;

/**
 * VertxHttpApi is a utility class responsible for configuring
 * the API routes for the Vert.x application.
 * It sets up the necessary HTTP endpoints and their respective handlers.
 */
public class VertxHttpApi {

    /**
     * Configures the API routes for the given router.
     * This method can be extended to include additional API endpoints
     * as the application grows.
     *
     * @param router The router instance to configure the API routes.
     */
    public static void configAPI(Router router) {
        // Initialize test API route
        testApi(router);
    }

    /**
     * Configures a test API route.
     * This method sets up a GET endpoint that responds to
     * requests at the /test path.
     *
     * @param router The router instance to which the test API route will be added.
     */
    private static void testApi(Router router) {
        // Register the GET handler for the /test endpoint
        router.get(createPath("/test")).handler(VertxHttpApi::testResponse);
    }

    /**
     * Handler method for the test API route.
     * This method generates a simple response indicating a successful
     * operation by calling SimpleResponse and sends it back to the client.
     *
     * @param routingContext The context of the current routing operation,
     *                      which contains request and response objects.
     */
    private static void testResponse(RoutingContext routingContext) {
        // End the response with a JSON object indicating success (error code 0)
        routingContext.response().end(SimpleResponse.createResponse(0).toString());
    }

    /**
     * Creates a full API path by prepending the configured URL prefix
     * to the provided path.
     *
     * @param path The relative path to be prefixed.
     * @return The full path as a string.
     */
    public static String createPath(String path) {
        // Retrieve the URL prefix from the server configuration
        String prefix = VertxServer.getInstance().config.get("url_prefix").getAsString();

        // Construct the full path by appending the provided path to the prefix
        return new StringBuilder()
                .append(prefix).append(path).toString();
    }
}
