package work.vietdefi.clean.http.test;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import static work.vietdefi.clean.http.VertxHttpApi.createPath;

public class TestApi {

    /**
     * Configures the API by setting up routes for GET and POST requests.
     * This method initializes the router with specific endpoints and their handlers.
     *
     * @param router the Router instance to which the routes will be attached.
     */
    public static void configAPI(Router router) {
        testGetMethod(router);  // Configure the GET route
        testPostMethod(router); // Configure the POST route
    }

    /**
     * Sets up the GET route handler.
     * This route listens for HTTP GET requests on the /test/get endpoint.
     *
     * @param router the Router instance used to attach the GET route.
     */
    private static void testGetMethod(Router router) {
        router.get(createPath("/test/get")) // Define GET route
                .handler(TestHandler::getMethod); // Attach handler for processing GET requests
    }

    /**
     * Sets up the POST route handler.
     * This route listens for HTTP POST requests on the /test/post endpoint.
     * It also enables handling of request bodies for the POST route.
     *
     * @param router the Router instance used to attach the POST route.
     */
    private static void testPostMethod(Router router) {
        router.post(createPath("/test/post")) // Define POST route
                .handler(BodyHandler.create(false)) // Enable body handling (with no file uploads)
                .handler(TestHandler::postMethod); // Attach handler for processing POST requests
    }
}
