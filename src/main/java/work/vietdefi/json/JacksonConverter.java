package work.vietdefi.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonConverter implements IJacksonConverter {
    private final ObjectMapper mapper;

    public JacksonConverter() {
        this.mapper = new ObjectMapper();
    }

    @Override
    public String toJsonString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON string", e);
        }
    }

    @Override
    public <T> T fromJsonString(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON string to object", e);
        }
    }

    @Override
    public JsonNode toJsonElement(Object object) {
        return mapper.valueToTree(object);
    }

    @Override
    public JsonNode toJsonElement(String json) {
        try {
            return mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON string to JsonNode", e);
        }
    }

    @Override
    public String fromJsonElementToString(JsonNode jsonNode) {
        return jsonNode.toString();
    }

    @Override
    public <T> T fromJsonElement(JsonNode jsonNode, Class<T> clazz) {
        try {
            return mapper.treeToValue(jsonNode, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JsonNode to object", e);
        }
    }
}
