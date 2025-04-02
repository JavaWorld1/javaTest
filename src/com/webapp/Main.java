package com.webapp;

import com.google.gson.Gson;
import com.webapp.model.*;
import com.webapp.storage.SqlStorage;

import java.time.Month;
import java.util.UUID;

public class Main {

    // смысл в том, чтобы получить данные из базы данных(строки) и на основе этих данных заполнить поля объекта класса Resume
    private final static SqlStorage SQL_STORAGE = new SqlStorage();
    private final static String UUID_1 = UUID.randomUUID().toString();

    // создание объекта Gson для преобразования объекта Resume в json
    private final static Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    static Resume RESUME = new Resume(UUID_1, "Name1");

    static {
        RESUME.addContact(ContactType.MAIL, "mail1@ya.ru");
        RESUME.addContact(ContactType.PHONE, "11111");

//        RESUME.addSection(SectionType.OBJECTIVE, new TextSection("Objective1"));
//        RESUME.addSection(SectionType.PERSONAL, new TextSection("Personal data"));
//        RESUME.addSection(SectionType.ACHIEVEMENT, new ListSection("Achievement11", "Achievement12", "Achievement13"));
//        RESUME.addSection(SectionType.QUALIFICATIONS, new ListSection("Java", "SQL", "JavaScript"));
//        RESUME.addSection(SectionType.EXPERIENCE,
//                new OrganizationSection(
//                        new Organization("Organization11", "http://Organization11.ru",
//                                new Organization.Position(2005, Month.JANUARY, "position1", "content1"),
//                                new Organization.Position(2001, Month.MARCH, 2005, Month.JANUARY, "position2", "content2")
//                        )
//                )
//        );
//        RESUME.addSection(SectionType.EDUCATION,
//                new OrganizationSection(
//                        new Organization("Institute", null,
//                                new Organization.Position(1996, Month.JANUARY, 2000,
//                                        Month.DECEMBER, "aspirant", null),
//                                new Organization.Position(2001, Month.MARCH, 2005,
//                                        Month.JANUARY, "student", "IT faculty")),
//                        new Organization("Organization12", "http://Organization12.ru")));
//        RESUME.addSection(SectionType.EXPERIENCE,
//                new OrganizationSection(
//                        new Organization("Organization2", "http://Organization2.ru",
//                                new Organization.Position(2015,
//                                        Month.JANUARY, "position1", "content1"))));
    }

    public static void main(String[] args) {

//        System.out.println(RESUME.getSection(SectionType.EXPERIENCE) + "\n");
//

        ///
//        //тест сериализации в json
//        System.out.println(GSON.toJson(SQL_STORAGE.get("7e9d4f75-9b39-4203-9863-c7843d0157e3")) + "\n");
//        System.out.println(JsonParser.write(RESUME.getSection(SectionType.EXPERIENCE)));

//        RESUME.addSection(SectionType.EXPERIENCE, JsonParser.read("""
//                {
//                  "type": "OrganizationSection",
//                  "content": {
//                    "organizations": [
//                      {
//                        "linkHomePage": {
//                          "name": "Organization11",
//                          "url": "http://Organization11.ru"
//                        },
//                        "positions": [
//                          {
//                            "startDate": "2005-01-01",
//                            "endDate": "3000-01-01",
//                            "title": "position1",
//                            "description": "content1"
//                          },
//                          {
//                            "startDate": "2001-03-01",
//                            "endDate": "2005-01-01",
//                            "title": "position2",
//                            "description": "content2"
//                          }
//                        ]
//                      }
//                    ]
//                  }
//                }"""));
//        System.out.println(SectionType.valueOf("EXPERIENCE"));
//        System.out.println(RESUME);
//        System.out.println();
        System.out.println(SQL_STORAGE.get("7e9d4f75-9b39-4203-9863-c7843d0157e3"));


//
//        //тест десериализации из json
////        System.out.println(GSON.fromJson(GSON.toJson(RESUME.getSection(SectionType.EXPERIENCE)), OrganizationSection.class));

        // создание объекта Organization с позициями с endYear
        // поля Organization: Link homepage, List<Position> positions
        // конструкторы Organization:
        // При создании объекта Organization передается несколько объектов типа Organization.Position. Эти объекты автоматически пакетируются в массив positions.
        // Затем этот массив передается в метод Arrays.asList(), который создает список из переданных элементов.
        //    public Organization(String name, String url, Organization.Position... positions) {
        //        this(new Link(name, url), Arrays.asList(positions));
        //    }

        //    public Organization(Link homePage, List<Organization.Position> positions) { // this(new Link(name, url), Arrays.asList(positions));
        //        this.homePage = homePage;
        //        this.positions = positions;
        //    }

        // конструктор Link:
        //    public Link(String name, String url) {
        //        this.name = name;
        //        this.url = url;
        //    }

        // toString у объекта Position: "Position(" + startDate + ',' + endDate + ',' + title + ',' + description + ')';
        // toString у объекта Organization: "Organization(" + linkHomePage + "," + positions + ')';
        // toString у объекта Link: "Link(" + name + ',' + url + ')';

        // Map<ContactType, String> contacts = new EnumMap<>(ContactType.class) - ContactType="PHONE", String="123456"
        Organization organization = new Organization("Organization11", "http://Organization11.ru",
                new Organization.Position(2005, Month.JANUARY, "position1", "content1"),
                new Organization.Position(2001, Month.MARCH, 2005, Month.JANUARY, "position2", "content2"));

        // создание объекта Position:
        Organization.Position position = new Organization.Position(2005, Month.JANUARY, "position1", "content1");

        // создание объекта OrganizationSection:
        OrganizationSection organizationSection = new OrganizationSection(new Organization("Organization11", "http://Organization11.ru",
                new Organization.Position(2005, Month.JANUARY, "position1", "content1"),
                new Organization.Position(2001, Month.MARCH, 2005, Month.JANUARY, "position2", "content2")));
        // organizationSection может содержать несколько объектов типа Organization.
//        System.out.println(organizationSection);
    }
}
