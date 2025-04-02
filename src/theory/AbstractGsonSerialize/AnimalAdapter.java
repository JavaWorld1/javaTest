package theory.AbstractGsonSerialize;

import com.google.gson.*;
import java.lang.reflect.Type;

class AnimalAdapter implements JsonSerializer<Animal>, JsonDeserializer<Animal> {
    private static final String TYPE_PROPERTY = "type";

    @Override
    public JsonElement serialize(Animal src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = context.serialize(src, src.getClass()).getAsJsonObject();
        if (src instanceof Dog) {
            jsonObject.addProperty(TYPE_PROPERTY, "Dog");
        } else if (src instanceof Cat) {
            jsonObject.addProperty(TYPE_PROPERTY, "Cat");
        }
        System.out.println("Serialized JSON with type: " + jsonObject);
        return jsonObject;
    }

    @Override
    public Animal deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement typeElement = jsonObject.get(TYPE_PROPERTY);

        if (typeElement == null) {
            System.out.println("JSON missing type field: " + jsonObject);
            throw new JsonParseException("Missing type field");
        }

        String type = typeElement.getAsString();
        System.out.println("Deserializing type: " + type);

        switch (type) {
            case "Dog":
                return context.deserialize(jsonObject, Dog.class);
            case "Cat":
                return context.deserialize(jsonObject, Cat.class);
            default:
                throw new JsonParseException("Unknown type: " + type);
        }
    }
}
