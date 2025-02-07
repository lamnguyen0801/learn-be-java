package work.vietdefi.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class GsonConverter implements IGsonConverter {
    private final Gson gson;

    public GsonConverter() {
        this.gson = new Gson();
    }

    @Override
    public String toJsonString(Object object) {
        return gson.toJson(object);
    }

    @Override
    public <T> T fromJsonString(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    @Override
    public JsonElement toJsonElement(Object object) {
        return gson.toJsonTree(object);
    }

    @Override
    public JsonElement toJsonElement(String json) {
        return JsonParser.parseString(json);
    }

    @Override
    public String fromJsonElementToString(JsonElement jsonElement) {
        return jsonElement.toString();
    }

    @Override
    public <T> T fromJsonElement(JsonElement jsonElement, Class<T> clazz) {
        return gson.fromJson(jsonElement, clazz);
    }
}
