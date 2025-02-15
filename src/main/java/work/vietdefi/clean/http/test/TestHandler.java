package work.vietdefi.clean.http.test;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import work.vietdefi.clean.services.common.SimpleResponse;
import work.vietdefi.util.json.GsonUtil;
import work.vietdefi.util.log.DebugLogger;

public class TestHandler {

    /**
     * Handles the HTTP GET request.
     * This method extracts "id" from the request headers and "key" from the query parameters.
     * It builds a JSON response containing these values and sends it back to the client.
     * If an exception occurs, it logs the error and returns a failure response.
     *
     * @param routingContext the context of the current HTTP request, providing access to request and response objects.
     */
    public static void getMethod(RoutingContext routingContext) {
        try {
            // Extract "id" from the request headers
            int id = Integer.parseInt(routingContext.request().getHeader("id"));

            // Extract "key" from the query parameters
            String key = routingContext.request().getParam("key");

            // Create a JSON object with the extracted values
            JsonObject json = new JsonObject();
            json.addProperty("id", id);
            json.addProperty("key", key);

            // Wrap the JSON object in a success response (e = 0)
            JsonObject response = SimpleResponse.createResponse(0, json);

            // Send the response to the client
            routingContext.response().end(response.toString());
        } catch (Exception e) {
            // Log the error and send a failure response (e = 1)
            DebugLogger.logger.error("", e);
            routingContext.response().end(SimpleResponse.createResponse(1).toString());
        }
    }

    /**
     * Handles the HTTP POST request.
     * This method retrieves the request body, converts it to a JSON object,
     * and sends it back to the client as a success response.
     * If an exception occurs during processing, it logs the error and sends a failure response.
     *
     * @param routingContext the context of the current HTTP request, providing access to request and response objects.
     */
    public static void postMethod(RoutingContext routingContext) {
        try {
            try {
                // Retrieve the request body as a string
                String body = routingContext.body().asString();

                // Convert the body string into a JSON object using Gson
                JsonObject json = GsonUtil.gsonConverter.toJsonElement(body).getAsJsonObject();

                // Wrap the JSON object in a success response (e = 0)
                JsonObject response = SimpleResponse.createResponse(0, json);

                // Send the response to the client
                routingContext.response().end(response.toString());
            } catch (Exception e) {
                // Log the error and send a failure response (e = 1)
                DebugLogger.logger.error("", e);
                routingContext.response().end(SimpleResponse.createResponse(1).toString());
            }
        } catch (Exception e) {
            // Log the error and send a failure response (e = 1)
            DebugLogger.logger.error("", e);
            routingContext.response().end(SimpleResponse.createResponse(1).toString());
        }
    }
}
