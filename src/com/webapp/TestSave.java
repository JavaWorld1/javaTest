package com.webapp;

import com.webapp.storage.SqlStorage;

public class TestSave {
    public static void main(String[] args) {
        SqlStorage storage = new SqlStorage("WEB-INF/config/resumes.properties");

//        Resume resume = new Resume("Иван Иванов");
//
//        // Контакты
//        resume.addContact(ContactType.PHONE, "+7 123 456 78 90");
//        resume.addContact(ContactType.MAIL, "ivan@example.com");
//
//        // Список — ListSection
//        resume.addSection(SectionType.ACHIEVEMENT, new ListSection(
//                "Сдал Java сертификацию",
//                "Разработал веб-приложение"
//        ));
//
//        // Простая секция — TextSection
//        resume.addSection(SectionType.OBJECTIVE, new TextSection("Хочу быть Java разработчиком"));
//        // OrganizationSection
//        Organization organization = new Organization("Java Online Projects", "https://javaops.ru",
//                new Organization.Position(2023, Month.JANUARY, "Стажёр", "Участвовал в проекте по разработке веб-сервиса"));
//
//        resume.addSection(SectionType.EXPERIENCE, new OrganizationSection(organization));
//
//        System.out.println(resume);
//
//        // Сохраняем в базу
//        storage.save(resume);
//
//        storage.get("d79a1bb7-9c09-41a2-910e-31e17d4a2321");

        System.out.println(storage.getAllSorted());
    }
}