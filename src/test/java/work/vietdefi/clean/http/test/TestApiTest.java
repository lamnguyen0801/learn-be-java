package work.vietdefi.clean.http.test;

import com.google.gson.JsonObject;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import work.vietdefi.clean.http.VertxHttpApi;
import work.vietdefi.clean.http.VertxServer;
import work.vietdefi.clean.services.common.SimpleResponse;
import work.vietdefi.util.log.DebugLogger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestApiTest {
    private static WebClient client; // Web client instance to perform API calls

    /**
     * Set up method that runs once before all tests in this class.
     * It configures the logging framework and starts the Vert.x server instance.
     */
    @BeforeAll
    public static void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        // Configure log4j using the specified XML configuration file
        DOMConfigurator.configure("config/log/log4j.xml");

        // Start the Vert.x server instance and wait for it to start
        Future<String> future = VertxServer.getInstance().start();
        CompletableFuture<String> completableFuture = future.toCompletionStage().toCompletableFuture();
        completableFuture.get(5, TimeUnit.SECONDS);  // Block the current thread until the server starts

        // Initialize the Vertx instance
        Vertx vertx = Vertx.vertx();

        // Retrieve the HTTP port from the VertxServer configuration
        int http_port = VertxServer.getInstance().config.get("http_port").getAsInt();

        // Create a WebClient for making requests to the server
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
     * Test method to validate the API response from the server for a GET request.
     * This method will verify that the GET endpoint returns the expected status code and body.
     */
    @Test
    public void testGet() throws InterruptedException, ExecutionException, TimeoutException {
        // Create the path for the GET request
        String path = VertxHttpApi.createPath("/test/get");
        // Append query parameters to the path
        String fullPath = new StringBuilder(path)
                .append("?key=testonly").toString();

        // Create a JSON object to represent expected response data
        JsonObject json = new JsonObject();
        json.addProperty("id", 10);
        json.addProperty("key", "testonly");

        // Send a GET request with a header and wait for the response
        CompletableFuture<HttpResponse<Buffer>> responseFuture = client.get(fullPath)
                .putHeader("id", "10")  // Add an optional header to the request
                .send()
                .toCompletionStage()
                .toCompletableFuture();

        // Retrieve the response, blocking until it completes
        HttpResponse<Buffer> response = responseFuture.get(5, TimeUnit.SECONDS);

        // Get the status code and response body
        int statusCode = response.statusCode();
        String responseBody = response.bodyAsString();

        // Assert that the response status code is 200 (OK)
        assertEquals(200, statusCode, "Expected status code to be 200");

        // Assert that the response body matches the expected JSON response
        String expectedBody = SimpleResponse.createResponse(0, json).toString();
        assertEquals(expectedBody, responseBody,
                "Response body does not match the expected JSON");
    }

    /**
     * Test method to validate the API response from the server for a POST request.
     * This method will verify that the POST endpoint returns the expected status code and body.
     */
    @Test
    public void testPost() throws InterruptedException, ExecutionException, TimeoutException {
        // Create the path for the POST request
        String path = VertxHttpApi.createPath("/test/post");

        // Create a JSON object to represent expected response data
        JsonObject json = new JsonObject();
        json.addProperty("id", 10);
        json.addProperty("key", "testonly");

        // Send a POST request with a JSON body and wait for the response
        CompletableFuture<HttpResponse<Buffer>> responseFuture = client.post(path)
                .sendBuffer(Buffer.buffer(json.toString())) // Send the JSON body
                .toCompletionStage()
                .toCompletableFuture();

        // Retrieve the response, blocking until it completes
        HttpResponse<Buffer> response = responseFuture.get(5, TimeUnit.SECONDS);

        // Get the status code and response body
        int statusCode = response.statusCode();
        String responseBody = response.bodyAsString();

        // Log the response details for debugging
        DebugLogger.logger.info("testApiResponse code = {} and body = {}", statusCode, responseBody);

        // Assert that the response status code is 200 (OK)
        assertEquals(200, statusCode, "Expected status code to be 200");

        // Assert that the response body matches the expected JSON response
        String expectedBody = SimpleResponse.createResponse(0, json).toString();
        assertEquals(expectedBody, responseBody,
                "Response body does not match the expected JSON");
    }
}
