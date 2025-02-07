package work.vietdefi.json;


import com.google.gson.JsonElement;


/**
 * IGsonConverter provides methods for converting between Java objects, JSON strings,
 * and JsonElement objects using the Gson library.
 */
public interface IGsonConverter {


    /**
     * Converts a Java object to its JSON string representation.
     *
     * @param object The Java object to convert.
     * @return The JSON string representation of the object.
     */
    String toJsonString(Object object);


    /**
     * Converts a JSON string to a Java object of the specified type.
     *
     * @param jsonString The JSON string to convert.
     * @param clazz      The class of the Java object to create.
     * @param <T>        The type of the Java object.
     * @return The Java object created from the JSON string.
     */
    <T> T fromJsonString(String jsonString, Class<T> clazz);


    /**
     * Converts a JSON string to a JsonElement object.
     *
     * @param jsonString The JSON string to convert.
     * @return The JsonElement representation of the JSON string.
     */
    JsonElement toJsonElement(String jsonString);


    /**
     * Converts a Java object to a JsonElement.
     *
     * @param object The Java object to convert.
     * @return The JsonElement representation of the Java object.
     */
    JsonElement toJsonElement(Object object);


    /**
     * Converts a JsonElement to its JSON string representation.
     *
     * @param jsonElement The JsonElement to convert.
     * @return The JSON string representation of the JsonElement.
     */
    String fromJsonElementToString(JsonElement jsonElement);


    /**
     * Converts a JsonElement to a Java object of the specified type.
     *
     * @param jsonElement The JsonElement to convert.
     * @param clazz       The class of the Java object to create.
     * @param <T>         The type of the Java object.
     * @return The Java object created from the JsonElement.
     */
    <T> T fromJsonElement(JsonElement jsonElement, Class<T> clazz);
}
