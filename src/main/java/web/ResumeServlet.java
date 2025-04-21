package web;

import exception.NotExistStorageException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.*;
import storage.SqlStorage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервлет для управления резюме (CRUD операции)
 */
@WebServlet("/resume")
public class ResumeServlet extends HttpServlet {
    private SqlStorage storage;

    @Override
    public void init() {
        // Инициализация хранилища при запуске сервлета
        storage = new SqlStorage();
    }

    /**
     * Обработка GET-запросов (просмотр, редактирование, удаление)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String uuid = request.getParameter("uuid"); // ID резюме
        String action = request.getParameter("action"); // Действие

        // Если действие не указано - показать список всех резюме
        if (action == null) {
            request.setAttribute("resumes", storage.getAllSorted());
            request.getRequestDispatcher("/WEB-INF/jsp/list.jsp").forward(request, response);
            return;
        }

        Resume resume;
        switch (action) {
            case "delete":
                // Удаление резюме
                storage.delete(uuid);
                response.sendRedirect("resume"); // Перенаправление на список
                return;

            case "view":
                // Просмотр резюме
                resume = storage.get(uuid);
                request.setAttribute("resume", resume);
                request.getRequestDispatcher("/WEB-INF/jsp/view.jsp").forward(request, response);
                return;

            case "edit":
                // Редактирование (нового или существующего)
                resume = (uuid != null) ? storage.get(uuid) : new Resume("");
                initEmptyFields(resume); // Заполняем пустые поля
                request.setAttribute("resume", resume);
                request.setAttribute("contactTypes", ContactType.values());
                request.setAttribute("sectionTypes", SectionType.values());
                request.getRequestDispatcher("/WEB-INF/jsp/edit.jsp").forward(request, response);
                return;

            case "clear":
                // Очистка всего хранилища (для тестов)
                storage.clear();
                response.sendRedirect("resume");
                return;

            default:
                throw new IllegalArgumentException("Action " + action + " is illegal");
        }
    }

    /**
     * Обработка POST-запросов (сохранение изменений)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        String uuid = request.getParameter("uuid");
        String fullName = request.getParameter("fullName").trim();

        if (fullName.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Имя не может быть пустым");
            return;
        }

        Resume resume;
        boolean isNew = (uuid == null || uuid.isEmpty());

        if (isNew) {
            // Создаем новое резюме
            resume = new Resume(fullName);
        } else {
            // Получаем существующее резюме для редактирования
            try {
                resume = storage.get(uuid);
                resume.setFullName(fullName);
            } catch (NotExistStorageException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Резюме не найдено");
                return;
            }
        }

        // Обновляем контакты и секции
        updateContacts(request, resume);
        updateSections(request, resume);

        // Сохраняем изменения
        if (isNew) {
            storage.save(resume);
        } else {
            storage.update(resume);
        }

        response.sendRedirect("resume");
    }

    /**
     * Обновление контактов резюме
     */
    private void updateContacts(HttpServletRequest request, Resume resume) {
        for (ContactType type : ContactType.values()) {
            String value = request.getParameter(type.name());
            if (value != null && !value.trim().isEmpty()) {
                // Добавляем/обновляем контакт
                resume.addContact(type, value.trim());
            } else {
                // Удаляем пустой контакт
                resume.getContacts().remove(type);
            }
        }
    }

    /**
     * Обновление секций резюме
     */
    private void updateSections(HttpServletRequest request, Resume resume) {
        for (SectionType type : SectionType.values()) {
            String value = request.getParameter(type.name());
            String[] values = request.getParameterValues(type.name());

            // Если секция не передана - удаляем ее
            if (value == null && values == null) {
                resume.getSections().remove(type);
                continue;
            }

            // Обработка разных типов секций
            switch (type) {
                case OBJECTIVE, PERSONAL -> {
                    // Текстовая секция
                    if (value != null && !value.trim().isEmpty()) {
                        resume.addSection(type, new TextSection(value.trim()));
                    } else {
                        resume.getSections().remove(type);
                    }
                }
                case ACHIEVEMENT, QUALIFICATIONS -> {
                    // Список (разделенный переносами строк)
                    String[] items = value.split("\\r?\\n");
                    List<String> cleanedItems = new ArrayList<>();

                    // Чистим каждый пункт от пробелов
                    for (String item : items) {
                        String trimmed = item.trim();
                        if (!trimmed.isEmpty()) {
                            cleanedItems.add(trimmed);
                        }
                    }

                    // Сохраняем только непустые списки
                    if (!cleanedItems.isEmpty()) {
                        resume.addSection(type, new ListSection(cleanedItems));
                    } else {
                        resume.getSections().remove(type);
                    }
                }
                case EXPERIENCE, EDUCATION -> {
                    // Секция с организациями
                    List<Organization> organizations = processOrganizations(request, type);
                    if (!organizations.isEmpty()) {
                        resume.addSection(type, new OrganizationSection(organizations));
                    } else {
                        resume.getSections().remove(type);
                    }
                }
            }
        }
    }

    /**
     * Обработка организаций в секции опыта/образования
     */
    private List<Organization> processOrganizations(HttpServletRequest request, SectionType type) {
        List<Organization> organizations = new ArrayList<>();
        String[] orgNames = request.getParameterValues(type.name());
        String[] urls = request.getParameterValues(type.name() + "url");

        if (orgNames == null) return organizations;

        for (int i = 0; i < orgNames.length; i++) {
            String name = orgNames[i].trim();
            if (!name.isEmpty()) {
                String url = (urls != null && i < urls.length) ? urls[i].trim() : "";
                List<Organization.Position> positions = processPositions(request, type, i);

                // Используем конструктор с Link и List<Position>
                organizations.add(new Organization(
                        new Link(name, url),
                        positions  // просто передаем List<Position>
                ));
            }
        }
        return organizations;
    }

    /**
     * Обработка позиций в организации
     */
    private List<Organization.Position> processPositions(HttpServletRequest request, SectionType type, int orgIndex) {
        List<Organization.Position> positions = new ArrayList<>();
        String[] titles = request.getParameterValues(type.name() + orgIndex + "title");
        if (titles == null) return positions;

        String[] startDates = request.getParameterValues(type.name() + orgIndex + "startDate");
        String[] endDates = request.getParameterValues(type.name() + orgIndex + "endDate");
        String[] descriptions = request.getParameterValues(type.name() + orgIndex + "description");

        // Обрабатываем каждую позицию
        for (int j = 0; j < titles.length; j++) {
            String title = titles[j].trim();
            if (!title.isEmpty()) {
                // Парсим даты (используем текущую дату по умолчанию)
                LocalDate startDate = parseDate(startDates, j);
                LocalDate endDate = parseDate(endDates, j);
                String description = (descriptions != null && j < descriptions.length) ? descriptions[j].trim() : "";

                positions.add(new Organization.Position(
                        startDate,
                        endDate,
                        title,
                        description
                ));
            }
        }
        return positions;
    }

    /**
     * Парсинг даты из строки (с обработкой ошибок)
     */
    private LocalDate parseDate(String[] dates, int index) {
        if (dates == null || index >= dates.length || dates[index] == null || dates[index].isEmpty()) {
            return LocalDate.now(); // Значение по умолчанию
        }
        try {
            return LocalDate.parse(dates[index]);
        } catch (Exception e) {
            return LocalDate.now(); // При ошибке парсинга
        }
    }

    /**
     * Инициализация пустых полей резюме (для формы редактирования)
     */
    private void initEmptyFields(Resume resume) {
        // Инициализация контактов
        for (ContactType type : ContactType.values()) {
            if (resume.getContact(type) == null) {
                resume.addContact(type, ""); // Пустое значение
            }
        }

        // Инициализация секций
        for (SectionType type : SectionType.values()) {
            if (resume.getSection(type) == null) {
                switch (type) {
                    case OBJECTIVE, PERSONAL -> resume.addSection(type, new TextSection("")); // Пустая текстовая секция
                    case ACHIEVEMENT, QUALIFICATIONS ->
                            resume.addSection(type, new ListSection(new ArrayList<>())); // Пустой список
                    case EXPERIENCE, EDUCATION -> resume.addSection(type, new OrganizationSection(
                            List.of(new Organization("", "", new Organization.Position[0]))
                    )); // Пустая организация
                }
            }
        }
    }
}