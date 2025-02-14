package work.vietdefi.clean.http;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import work.vietdefi.clean.services.common.SimpleResponse;
import work.vietdefi.util.log.DebugLogger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * VertxServerTest is a test class for testing the Vert.x server functionality.
 * It sets up the logging configuration and starts the server before all tests
 * and contains test methods for validating API responses.
 */
@ExtendWith(VertxExtension.class)
public class VertxServerTest {
    private static Vertx vertx; // Vertx instance for tests
    private static WebClient client; // Web client to make API calls

    /**
     * Setup method that runs once before all tests in this class.
     * It performs the following tasks:
     * 1. Configures the logging framework using the specified XML configuration file.
     * 2. Starts the Vert.x server instance and ensures it is running by blocking until the startup completes.
     * 3. Initializes a new Vert.x instance.
     * 4. Retrieves the HTTP port from the Vert.x server configuration.
     * 5. Creates a WebClient with the configured HTTP port for making HTTP requests during tests.
     */
    @BeforeAll
    public static void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        // Configure log4j using the specified XML configuration file
        DOMConfigurator.configure("config/log/log4j.xml");

        // Start the Vert.x server instance and wait for completion
        Future<String> future = VertxServer.getInstance().start();
        CompletableFuture<String> completableFuture = future.toCompletionStage().toCompletableFuture();
        completableFuture.get();  // Block the current thread until the server starts

        // Initialize a new Vert.x instance
        vertx = Vertx.vertx();

        // Retrieve the HTTP port from the Vert.x server configuration
        int http_port = VertxServer.getInstance().config.get("http_port").getAsInt();

        // Create a WebClient for making HTTP requests
        client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(http_port));
    }

    /**
     * Tear down method that runs once after all tests in this class.
     * Currently empty, but can be used to clean up resources or stop the server.
     */
    @AfterAll
    public static void tearDown() {
        // Add cleanup code here, such as stopping the server or closing resources
    }

    /**
     * Test method to validate the API response from the server.
     * This test performs the following steps:
     * 1. Constructs the API endpoint path.
     * 2. Sends a synchronous GET request to the specified endpoint.
     * 3. Retrieves and logs the response status code and body.
     * 4. Asserts that the response status code is 200 (OK).
     * 5. Asserts that the response body matches the expected JSON structure.
     */
    @Test
    public void testApiResponse() throws InterruptedException, ExecutionException, TimeoutException {
        // Construct the API endpoint path
        String path = VertxHttpApi.createPath("/test");

        // Send a synchronous GET request to the /test API endpoint and block until completion
        CompletableFuture<HttpResponse<Buffer>> responseFuture = client.get(path)
                .send()
                .toCompletionStage()
                .toCompletableFuture();

        // Retrieve the response, blocking until it completes
        HttpResponse<Buffer> response = responseFuture.get();

        // Extract status code and body from the response
        int statusCode = response.statusCode();
        String responseBody = response.bodyAsString();

        // Log the status code and body for debugging
        DebugLogger.logger.info("testApiResponse code = {} and body = {}", statusCode, responseBody);

        // Assert that the status code is 200 (OK)
        assertEquals(200, statusCode, "Expected status code to be 200");

        // Assert that the response body matches the expected JSON structure
        assertEquals(SimpleResponse.createResponse(0).toString(), responseBody,
                "Response body does not match the expected JSON");
    }
}
