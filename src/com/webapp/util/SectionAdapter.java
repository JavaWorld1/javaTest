package com.webapp.util;

import com.google.gson.*;
import com.webapp.model.OrganizationSection;
import com.webapp.model.Section;

import java.lang.reflect.Type;

public class SectionAdapter implements JsonSerializer<Section>, JsonDeserializer<Section> {

    // строка  в объект
    @Override
    public Section deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();

        switch (type) {
            case "OrganizationSection":
                return context.deserialize(jsonObject.get("content"), OrganizationSection.class);
            default:
                throw new JsonParseException("Unknown section type: " + type);
        }
    }

    // объект в строку
    @Override
    public JsonElement serialize(Section src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        if (src instanceof OrganizationSection) {
            jsonObject.addProperty("type", "OrganizationSection");
            jsonObject.add("content", context.serialize(src, OrganizationSection.class));
        } else {
            throw new JsonParseException("Unknown section type: " + src.getClass().getName());
        }
        return jsonObject;
    }
}
