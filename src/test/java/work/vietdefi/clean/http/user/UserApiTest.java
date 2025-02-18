package work.vietdefi.clean.http.user;

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
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import work.vietdefi.clean.http.VertxHttpApi;
import work.vietdefi.clean.http.VertxServer;
import work.vietdefi.clean.services.SharedServices;
import work.vietdefi.util.json.GsonUtil;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserApiTest class contains test cases for user registration, login, and retrieval APIs.
 * It uses Vert.x WebClient to interact with HTTP endpoints provided by the server.
 */

public class UserApiTest {
    /** Random instance used to generate unique usernames for testing. */
    private static Random random;

    /** Stores the user ID returned from registration to validate further operations. */
    private static String USER_ID;

    /** Stores the registered username to be reused in login tests. */
    private static String USERNAME;

    /** Stores the authentication token returned during login. */
    private static String TOKEN;

    /** Fixed password used for all registration and login tests. */
    private static final String PASSWORD = "123456";

    /** WebClient instance used for making HTTP requests to the server. */
    private static WebClient client;

    /**
     * Sets up the environment for testing by starting the server and initializing the WebClient.
     *
     * @throws ExecutionException   if the future computation fails.
     * @throws InterruptedException if the current thread is interrupted while waiting.
     * @throws TimeoutException     if the server does not start within the given time.
     */
    @BeforeAll
    public static void setUp() throws ExecutionException, InterruptedException, TimeoutException, IOException {
        // Configure the log system using an XML configuration file.
        DOMConfigurator.configure("config/log/log4j.xml");

        // Initialize shared services required for the application.
        SharedServices.init();

        // Start the server asynchronously and block until it is ready.
        Future<String> future = VertxServer.getInstance().start();
        CompletableFuture<String> completableFuture = future.toCompletionStage().toCompletableFuture();
        completableFuture.get(5, TimeUnit.SECONDS); // Wait for the server to start.

        // Create a Vert.x instance to be used with the WebClient.
        Vertx vertx = Vertx.vertx();

        // Retrieve the HTTP port from the server configuration.
        int http_port = VertxServer.getInstance().config.get("http_port").getAsInt();

        // Initialize the WebClient with the HTTP port.
        client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(http_port));

        // Initialize the Random instance to generate random usernames.
        random = new Random();
    }
    /**
     * Cleans up resources after all tests have been executed.
     */
    @AfterAll
    public static void tearDown() {
        // Stop server
        Future<Void> stopFuture = VertxServer.getInstance().stop();

        stopFuture.onSuccess(v -> {
            System.out.println("Server has been stopped successfully!");
        }).onFailure(err -> {
            System.err.println("Fail to stop server: " + err.getMessage());
        });

    }

    /**
     * Tests the registration of a new user by sending a POST request to the server.
     *
     * @throws InterruptedException if the current thread is interrupted while waiting.
     * @throws ExecutionException   if the future computation fails.
     * @throws TimeoutException     if the request does not complete in time.
     */
    @Test
    @Order(1)
    public void testRegisterUser() throws InterruptedException, ExecutionException, TimeoutException {
        // Build the registration API path.
        String path = VertxHttpApi.createPath("/user/register");

        // Generate a unique username using the Random instance.
        USERNAME = new StringBuilder("user_").append(random.nextInt(1000000)).toString();

        // Create the JSON payload for the registration request.
        JsonObject json = new JsonObject();
        json.addProperty("username", USERNAME);
        json.addProperty("password", PASSWORD);

        // Send the POST request to register the user.
        CompletableFuture<HttpResponse<Buffer>> responseFuture = client.post(path)
                .sendBuffer(Buffer.buffer(json.toString()))
                .toCompletionStage()
                .toCompletableFuture();

        // Retrieve the response from the server.
        HttpResponse<Buffer> response = responseFuture.get(5, TimeUnit.SECONDS);

        // Verify that the response status is 200 (OK).
        assertEquals(200, response.statusCode());

        // Parse the response body into a JSON object.
        String responseBody = response.bodyAsString();
        JsonObject responseJson = GsonUtil.gsonConverter.toJsonElement(responseBody).getAsJsonObject();

        // Check that there are no errors in the response (error code 0).
        assertEquals(0, responseJson.get("e").getAsInt());

        // Extract the user data from the response and validate its contents.
        JsonObject user = responseJson.getAsJsonObject("d");
        assertNotNull(user.get("user_id"));
        assertNotNull(user.get("username"));
        assertNull(user.get("password"));
        assertNotNull(user.get("token"));
        assertNotNull(user.get("token_expired"));

        // Store the token and user ID for use in subsequent tests.
        TOKEN = user.get("token").getAsString();
        USER_ID = user.get("user_id").getAsString();
    }


    /**
     * Tests registering a user with a duplicate username to verify error handling.
     *
     * @throws InterruptedException if the current thread is interrupted while waiting.
     * @throws ExecutionException   if the future computation fails.
     * @throws TimeoutException     if the request does not complete in time.
     */
    @Test
    @Order(2)
    public void testRegisterDuplicateUserName() throws InterruptedException, ExecutionException, TimeoutException {
        // Reuse the previously registered username.
        String path = VertxHttpApi.createPath("/user/register");
        JsonObject json = new JsonObject();
        json.addProperty("username", USERNAME);
        json.addProperty("password", PASSWORD);

        // Send a POST request to attempt registration with the duplicate username.
        CompletableFuture<HttpResponse<Buffer>> responseFuture = client.post(path)
                .sendBuffer(Buffer.buffer(json.toString()))
                .toCompletionStage()
                .toCompletableFuture();

        // Retrieve the response.
        HttpResponse<Buffer> response = responseFuture.get(5, TimeUnit.SECONDS);

        // Verify that the response status is 200 (OK).
        assertEquals(200, response.statusCode());

        // Parse the response JSON and check for the duplicate username error (error code 10).
        JsonObject responseJson = GsonUtil.gsonConverter.toJsonElement(response.bodyAsString()).getAsJsonObject();
        assertEquals(10, responseJson.get("e").getAsInt());
    }

    /**
     * Tests logging in with valid credentials.
     *
     * @throws InterruptedException if the current thread is interrupted while waiting.
     * @throws ExecutionException   if the future computation fails.
     * @throws TimeoutException     if the request does not complete in time.
     */
    @Test
    @Order(3)
    public void testUserLogin() throws InterruptedException, ExecutionException, TimeoutException {
        // Build the login API path.
        String path = VertxHttpApi.createPath("/user/login");

        // Create the JSON payload for the login request.
        JsonObject json = new JsonObject();
        json.addProperty("username", USERNAME);
        json.addProperty("password", PASSWORD);

        // Send the POST request to log in.
        CompletableFuture<HttpResponse<Buffer>> responseFuture = client.post(path)
                .sendBuffer(Buffer.buffer(json.toString()))
                .toCompletionStage()
                .toCompletableFuture();

        // Retrieve the response.
        HttpResponse<Buffer> response = responseFuture.get(5, TimeUnit.SECONDS);

        // Verify that the response status is 200 (OK).
        assertEquals(200, response.statusCode());

        // Parse the response JSON and check for a successful login (error code 0).
        JsonObject responseJson = GsonUtil.gsonConverter.toJsonElement(response.bodyAsString()).getAsJsonObject();
        assertEquals(0, responseJson.get("e").getAsInt());
    }
    /**
     * Test the user login API with an invalid username.
     *
     * <p>Verifies that the API returns the appropriate error code when
     * a user attempts to log in with a non-existent or incorrect username.</p>
     *
     * @throws InterruptedException if the thread is interrupted while waiting.
     * @throws ExecutionException   if an error occurs during the asynchronous operation.
     * @throws TimeoutException     if the request exceeds the time limit.
     */
    @Test
    @Order(4)
    public void testUserLoginInvalidUsername() throws InterruptedException, ExecutionException, TimeoutException {
        // Create the login API path.
        String path = VertxHttpApi.createPath("/user/login");

        // Prepare JSON payload with an invalid username and valid password.
        JsonObject json = new JsonObject();
        json.addProperty("username", "***"); // Simulate non-existent username.
        json.addProperty("password", PASSWORD);

        // Send a POST request with the JSON body.
        CompletableFuture<HttpResponse<Buffer>> responseFuture = client.post(path)
                .sendBuffer(Buffer.buffer(json.toString())) // Send the JSON body.
                .toCompletionStage()
                .toCompletableFuture();

        // Retrieve the server's response.
        HttpResponse<Buffer> response = responseFuture.get(5, TimeUnit.SECONDS);

        // Assert that the HTTP status code is 200, indicating request was processed.
        assertEquals(200, response.statusCode());

        // Parse the response body into a JSON object.
        String responseBody = response.bodyAsString();
        JsonObject responseJson = GsonUtil.gsonConverter.toJsonElement(responseBody).getAsJsonObject();

        // Verify that the error code is 10, representing invalid username or login failure.
        assertEquals(10, responseJson.get("e").getAsInt());
    }

    /**
     * Test the user login API with an invalid password.
     *
     * <p>Validates that the API returns the correct error code when
     * the user attempts to log in with the correct username but an incorrect password.</p>
     *
     * @throws InterruptedException if the thread is interrupted while waiting.
     * @throws ExecutionException   if an error occurs during the asynchronous operation.
     * @throws TimeoutException     if the request exceeds the time limit.
     */
    @Test
    @Order(5)
    public void testUserLoginInvalidPassword() throws InterruptedException, ExecutionException, TimeoutException {
        // Create the login API path.
        String path = VertxHttpApi.createPath("/user/login");

        // Prepare the JSON payload with a valid username and an invalid password.
        JsonObject json = new JsonObject();
        json.addProperty("username", USERNAME); // Use the valid registered username.
        json.addProperty("password", "***");    // Simulate an invalid password.

        // Send a POST request with the JSON body.
        CompletableFuture<HttpResponse<Buffer>> responseFuture = client.post(path)
                .sendBuffer(Buffer.buffer(json.toString())) // Send the JSON body.
                .toCompletionStage()
                .toCompletableFuture();

        // Retrieve the server's response.
        HttpResponse<Buffer> response = responseFuture.get(5, TimeUnit.SECONDS);

        // Assert that the HTTP status code is 200, indicating the request was processed.
        int statusCode = response.statusCode();
        assertEquals(200, statusCode);

        // Parse the response body into a JSON object.
        String responseBody = response.bodyAsString();
        JsonObject responseJson = GsonUtil.gsonConverter.toJsonElement(responseBody).getAsJsonObject();

        // Verify that the error code is 11, representing invalid password.
        int error = responseJson.get("e").getAsInt();
        assertEquals(11, error);
    }

    /**
     * Test the user retrieval API with a valid token.
     *
     * <p>Ensures that the API returns user details correctly when a valid token
     * is provided. Verifies that the response contains the expected user data
     * and no errors are reported.</p>
     *
     * @throws InterruptedException if the thread is interrupted while waiting.
     * @throws ExecutionException   if an error occurs during the asynchronous operation.
     * @throws TimeoutException     if the request exceeds the time limit.
     */
    @Test
    @Order(6)
    public void testUserGet() throws InterruptedException, ExecutionException, TimeoutException {
        // Create the path for the user retrieval API.
        String path = VertxHttpApi.createPath("/user/get");

        // Send a GET request with the token in the header.
        CompletableFuture<HttpResponse<Buffer>> responseFuture = client.get(path)
                .putHeader("token", TOKEN)  // Include the valid token in the request header.
                .send()  // Execute the request.
                .toCompletionStage()
                .toCompletableFuture();

        // Retrieve the server's response.
        HttpResponse<Buffer> response = responseFuture.get(5, TimeUnit.SECONDS);

        // Assert that the HTTP status code is 200, indicating the request was processed successfully.
        int statusCode = response.statusCode();
        assertEquals(200, statusCode);

        // Parse the response body into a JSON object.
        String responseBody = response.bodyAsString();
        JsonObject responseJson = GsonUtil.gsonConverter.toJsonElement(responseBody).getAsJsonObject();

        // Verify that there are no errors (error code 0).
        int error = responseJson.get("e").getAsInt();
        assertEquals(0, error);

        // Extract the user data from the response.
        JsonObject user = responseJson.getAsJsonObject("d");

        // Ensure all expected fields are present and not null.
        assertNotNull(user.get("user_id"));
        assertNotNull(user.get("username"));
        assertNull(user.get("password"));
        assertNotNull(user.get("token"));
        assertNotNull(user.get("token_expired"));

        // Confirm the username matches the expected value.
        assertEquals(USERNAME, user.get("username").getAsString());
    }
    /**
     * Test the user retrieval API with an invalid token.
     *
     * <p>This test ensures that when an invalid token is provided, the API
     * responds appropriately with an error code, indicating the token is not valid.</p>
     *
     * @throws InterruptedException if the thread is interrupted while waiting.
     * @throws ExecutionException   if an error occurs during the asynchronous operation.
     * @throws TimeoutException     if the request exceeds the time limit.
     */
    @Test
    @Order(7)
    public void testUserGetInvalidToken() throws InterruptedException, ExecutionException, TimeoutException {
        // Create the path for the user retrieval API.
        String path = VertxHttpApi.createPath("/user/get");

        // Send a GET request with an invalid token in the header.
        CompletableFuture<HttpResponse<Buffer>> responseFuture = client.get(path)
                .putHeader("token", "***")  // Include an invalid token in the request header.
                .send()  // Execute the request.
                .toCompletionStage()
                .toCompletableFuture();

        // Retrieve the server's response.
        HttpResponse<Buffer> response = responseFuture.get(5, TimeUnit.SECONDS);

        // Assert that the HTTP status code is 200, indicating the request was processed.
        int statusCode = response.statusCode();
        assertEquals(200, statusCode);

        // Parse the response body into a JSON object.
        String responseBody = response.bodyAsString();
        JsonObject responseJson = GsonUtil.gsonConverter.toJsonElement(responseBody).getAsJsonObject();

        // Verify that the error code returned is 10, indicating an invalid token.
        int error = responseJson.get("e").getAsInt();
        assertEquals(2, error);
    }

    /**
     * Test the user retrieval API with an expired token.
     *
     * <p>This test simulates a scenario where the user's token has expired. It verifies
     * that the API responds correctly when a valid token is used, but the token itself
     * has expired, returning an appropriate error code.</p>
     *
     * @throws Exception if an error occurs during the database update or the request.
     */
    @Test
    @Order(8)
    public void testUserGetTokenExpired() throws Exception {
        // Update the database to set the token as expired for the current user.
        SharedServices.sqlJavaBridge.update("UPDATE "
                + SharedServices.USER_TABLE + " SET token_expired = ? WHERE user_id = ?", 0, USER_ID);

        // Create the path for the user retrieval API.
        String path = VertxHttpApi.createPath("/user/get");

        // Send a GET request with the user's token in the header.
        CompletableFuture<HttpResponse<Buffer>> responseFuture = client.get(path)
                .putHeader("token", TOKEN)  // Include the potentially expired token in the request header.
                .send() // Execute the request.
                .toCompletionStage()
                .toCompletableFuture();

        // Retrieve the server's response.
        HttpResponse<Buffer> response = responseFuture.get(5, TimeUnit.SECONDS);

        // Assert that the HTTP status code is 200, indicating the request was processed.
        int statusCode = response.statusCode();
        assertEquals(200, statusCode);

        // Parse the response body into a JSON object.
        String responseBody = response.bodyAsString();
        JsonObject responseJson = GsonUtil.gsonConverter.toJsonElement(responseBody).getAsJsonObject();

        // Verify that the error code returned is 2, indicating the token has expired.
        int error = responseJson.get("e").getAsInt();
        assertEquals(2, error);
    }
}
