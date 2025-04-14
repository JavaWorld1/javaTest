package util;

import com.google.gson.*;
import model.Section;

import java.lang.reflect.Type;
import java.time.LocalDate;

public class JsonParser {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Section.class, new SectionAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting()
            .create();

    public static <T> T read(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static <T> String write(T object, Class<T> clazz) {
        return GSON.toJson(object, clazz );
    }

    // Вложенный адаптер для LocalDate
    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString()); // Формат: yyyy-MM-dd
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return LocalDate.parse(json.getAsString());
        }
    }
}
//public static String write(Object object) {
//    return GSON.toJson(object);
//}