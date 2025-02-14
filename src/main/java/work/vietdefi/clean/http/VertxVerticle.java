package work.vietdefi.clean.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * VertxVerticle is a class that extends AbstractVerticle,
 * representing a Vert.x verticle responsible for setting up
 * an HTTP server with a router for handling web requests.
 */
public class VertxVerticle extends AbstractVerticle {

    // Instance of the HTTP server
    private HttpServer httpServer;

    /**
     * The start method is called when the verticle is deployed.
     * It initializes the router, configures CORS, and sets up the HTTP server.
     */
    @Override
    public void start() {
        // Create a router instance to handle incoming HTTP requests
        Router router = Router.router(vertx);

        // Configure Cross-Origin Resource Sharing (CORS) settings
        crossAccessControl(router);

        // Configure API routes and handlers
        VertxHttpApi.configAPI(router);

        // Retrieve the HTTP port from the VertxServer configuration
        int http_port = VertxServer.getInstance().config.get("http_port").getAsInt();

        // Create and start the HTTP server with the specified port and request handler
        httpServer = vertx.createHttpServer()
                .requestHandler(router)  // Set the request handler to the router
                .listen(http_port)       // Listen on the specified port
                .result();               // Block until the server is fully started
    }

    /**
     * The stop method is called when the verticle is undeployed.
     * It closes the HTTP server to free up resources.
     */
    @Override
    public void stop() {
        // Close the HTTP server if it has been initialized
        if(httpServer != null) httpServer.close();
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) for the given router.
     * This allows web applications running on different domains to
     * communicate with the server.
     *
     * @param router The router instance to configure CORS for.
     */
    public void crossAccessControl(Router router) {
        // Set of allowed headers for CORS
        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("*"); // Allow all headers

        // Set of allowed HTTP methods for CORS
        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);      // Allow GET requests
        allowedMethods.add(HttpMethod.POST);     // Allow POST requests
        allowedMethods.add(HttpMethod.OPTIONS);  // Allow OPTIONS requests

        // Configure the CORS handler for the router
        router.route().handler(CorsHandler.create()
                .allowedHeaders(allowedHeaders)  // Set allowed headers
                .allowedMethods(allowedMethods)    // Set allowed methods
                .allowCredentials(true));          // Allow credentials in CORS requests
    }
}
