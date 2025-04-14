package util;

import com.google.gson.*;
import model.ListSection;
import model.OrganizationSection;
import model.Section;
import model.TextSection;

import java.lang.reflect.Type;
import java.util.Objects;

public class SectionAdapter implements JsonSerializer<Section>, JsonDeserializer<Section> {

    @Override
    public Section deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();

        return switch (type) {
            case "OrganizationSection" -> context.deserialize(jsonObject.get("content"), OrganizationSection.class);
            case "TextSection" -> context.deserialize(jsonObject.get("content"), TextSection.class);
            case "ListSection" -> context.deserialize(jsonObject.get("content"), ListSection.class);
            default ->
                // Добавьте другие типы секций по мере необходимости
                    throw new JsonParseException("Unknown section type: " + type);
        };
    }

    @Override
    public JsonElement serialize(Section src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        switch (src) {
            case OrganizationSection organizationSection -> {
                jsonObject.addProperty("type", "OrganizationSection");
                jsonObject.add("content", context.serialize(src, OrganizationSection.class));
            }
            case TextSection textSection -> {
                jsonObject.addProperty("type", "TextSection");
                jsonObject.add("content", context.serialize(src, TextSection.class));
            }
            case ListSection listSection -> {
                jsonObject.addProperty("type", "ListSection");
                jsonObject.add("content", context.serialize(src, ListSection.class));
            }
            case null, default ->
                    throw new JsonParseException("Unknown section type: " + Objects.requireNonNull(src).getClass().getName());
        }
        return jsonObject;
    }
}
