package com.webapp;

import com.webapp.model.*;
import com.webapp.util.JsonParser;

import java.time.Month;
import java.util.Arrays;

public class SerializeTest {
    public static void main(String[] args) {
        // Создание тестового объекта OrganizationSection
        Organization organization = new Organization(
                new Link("Organization11", "http://Organization11.ru"),
                Arrays.asList(
                        new Organization.Position(2005, Month.JANUARY, "Position 1", "Description of position 1"),
                        new Organization.Position(2001, Month.MARCH, 2005, Month.JANUARY, "Position 2", "Description of position 2")
                )
        );

        OrganizationSection organizationSection = new OrganizationSection(organization);

        // Сериализация объекта в JSON строку
        String json = JsonParser.write(organizationSection);
        System.out.println("Serialized JSON:");
        System.out.println(json);

        // Десериализация обратно в объект Section
        Section deserializedSection = JsonParser.read(json, Section.class);
        System.out.println("\nDeserialized Object:");
        System.out.println(deserializedSection);
    }
}
