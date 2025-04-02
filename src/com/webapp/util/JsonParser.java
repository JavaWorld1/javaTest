package com.webapp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webapp.model.Section;

import java.time.LocalDate;

public class JsonParser {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Section.class, new SectionAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    public static Section read(String content) { // строка  в объект
        return GSON.fromJson(content, Section.class);
    }

    public static String write(Section section) {  // объект в строку
        return GSON.toJson(section, Section.class);
    }
}
