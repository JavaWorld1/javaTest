package com.webapp;

import com.google.gson.Gson;
import com.webapp.model.*;
import com.webapp.storage.SqlStorage;

import java.time.Month;
import java.util.UUID;

public class Main {

    // смысл в том, чтобы получить данные из базы данных(строки) и на основе этих данных заполнить поля объекта класса Resume
    private final static SqlStorage SQL_STORAGE = new SqlStorage("/WEB-INF/config/resumes.properties");
    private final static String UUID_1 = UUID.randomUUID().toString();

    public static Gson getGSON() {
        return GSON;
    }

    // создание объекта Gson для преобразования объекта Resume в json
    private final static Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    static Resume RESUME = new Resume(UUID_1, "Name1");

    static {
        RESUME.addContact(ContactType.MAIL, "mail1@ya.ru");
        RESUME.addContact(ContactType.PHONE, "11111");

        RESUME.addSection(SectionType.OBJECTIVE, new TextSection("Objective1"));
        RESUME.addSection(SectionType.PERSONAL, new TextSection("Personal data"));
        RESUME.addSection(SectionType.ACHIEVEMENT, new ListSection("Achievement11", "Achievement12", "Achievement13"));
        RESUME.addSection(SectionType.QUALIFICATIONS, new ListSection("Java", "SQL", "JavaScript"));
        RESUME.addSection(SectionType.EXPERIENCE,
                new OrganizationSection(
                        new Organization("Organization11", "http://Organization11.ru",
                                new Organization.Position(2005, Month.JANUARY, "position1", "content1"),
                                new Organization.Position(2001, Month.MARCH, 2005, Month.JANUARY, "position2", "content2")
                        )
                )
        );
        RESUME.addSection(SectionType.EDUCATION,
                new OrganizationSection(
                        new Organization("Institute", null,
                                new Organization.Position(1996, Month.JANUARY, 2000,
                                        Month.DECEMBER, "aspirant", null),
                                new Organization.Position(2001, Month.MARCH, 2005,
                                        Month.JANUARY, "student", "IT faculty")),
                        new Organization("Organization12", "http://Organization12.ru")));
        RESUME.addSection(SectionType.EXPERIENCE,
                new OrganizationSection(
                        new Organization("Organization2", "http://Organization2.ru",
                                new Organization.Position(2015,
                                        Month.JANUARY, "position1", "content1"))));
    }

    public static void main(String[] args) {
        System.out.println(SQL_STORAGE.get("7e9d4f75-9b39-4203-9863-c7843d0157e3"));
//        System.out.println(SQL_STORAGE.get("8ba3bcff-72d5-488c-80d1-fb91d7c1309b"));
    }
}
