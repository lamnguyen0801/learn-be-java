package work.vietdefi.clean.services.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * SimpleResponse provides utility methods to create and evaluate
 * standardized JSON responses. It simplifies response generation
 * with error codes and optional data elements.
 */
public class SimpleResponse {

    /**
     * Creates a JSON response with an error code and data payload.
     *
     * @param error The error code, where 0 typically indicates success
     *              and non-zero values indicate specific errors.
     * @param data  The additional data to include in the response,
     *              wrapped as a JsonElement (can be an object, array, or primitive).
     * @return A JsonObject representing the response with the structure:
     *         {
     *           "e": errorCode,
     *           "d": dataPayload
     *         }
     */
    public static JsonObject createResponse(int error, JsonElement data) {
        JsonObject json = new JsonObject();
        json.addProperty("e", error);  // Adds the error code to the response.
        json.add("d", data);           // Adds the data payload, if present.
        return json;
    }

    /**
     * Creates a JSON response with only an error code, without any data payload.
     *
     * @param error The error code, where 0 usually indicates success.
     * @return A JsonObject representing the response with the structure:
     *         {
     *           "e": errorCode
     *         }
     */
    public static JsonObject createResponse(int error) {
        JsonObject json = new JsonObject();
        json.addProperty("e", error);  // Adds the error code to the response.
        return json;
    }

    /**
     * Checks whether the given response indicates success.
     * A response is considered successful if the "e" field (error code) is 0.
     *
     * @param response The JsonObject response to check.
     * @return true if the response has an error code of 0, indicating success;
     *         false otherwise.
     * @throws NullPointerException if the response is null or does not contain the "e" field.
     */
    public static boolean isSuccess(JsonObject response) {
        // Returns true if the error code is 0 (success).
        return response.get("e").getAsInt() == 0;
    }
}
