package work.vietdefi.json;

import com.fasterxml.jackson.databind.JsonNode;

public interface IJacksonConverter {
    String toJsonString(Object object);
    <T> T fromJsonString(String json, Class<T> clazz);
    JsonNode toJsonElement(Object object);
    JsonNode toJsonElement(String json);
    String fromJsonElementToString(JsonNode jsonNode);
    <T> T fromJsonElement(JsonNode jsonNode, Class<T> clazz);
}
