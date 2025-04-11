package com.webapp.storage;

import com.webapp.exception.NotExistStorageException;
import com.webapp.exception.StorageException;
import com.webapp.model.*;
import com.webapp.util.JsonParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SqlStorage implements Storage {

    private Properties properties = new Properties();
    private String configFilePath;

    // Конструктор для получения пути к файлу конфигурации
    public SqlStorage(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public Resume get(String uuid) throws NotExistStorageException {
        Resume resume = null;
        try (FileInputStream fileInputStream = new FileInputStream("WEB-INF/config/resumes.properties")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password"))) {
            // Начало транзакции
            // устанавливает ручное управление транзакцией, и после успешного выполнения операций метода,
            // вызывается connection.commit() для фиксации изменений. В случае возникновения исключения,
            // вызывается connection.rollback() для отката транзакции.

            try {
                connection.setAutoCommit(false); // устанавливает ручное управление коммитом транзакции

                // получение fullname того uuid, который мы передали в метод sqlStorage.get()
                // далее создаем объект класса Resume и заполняем его поля fullName и uuid
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM resume WHERE uuid =?")) {
                    preparedStatement.setString(1, uuid);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (!resultSet.next()) { // resultSet.next() вернет false, что будет означать, что в результате запроса нет строк.
                            // Однако, в этом случае мы сразу бросаем исключение NotExistStorageException,
                            // не пытаясь читать данные из результата запроса.
                            throw new NotExistStorageException(uuid);
                        }
                        resume = new Resume(uuid, resultSet.getString("full_name")); // получение данных из таблицы resume
                    }
                }

                // добавление контактов пользователя в резюме
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM contact WHERE resume_uuid =?")) {
                    preparedStatement.setString(1, uuid);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) { // Переместили вызов resultSet.next() внутрь цикла
                            String value = resultSet.getString("value");
                            ContactType type = ContactType.valueOf(resultSet.getString("type")); // получение типа контакта
                            if (value != null) {
                                resume.addContact(type, value); // добавление контакта
                            } else {
                                throw new NotExistStorageException(uuid);
                            }
                        }
                    } catch (SQLException e) {
                        throw new StorageException(e);
                    }
                }

                // добавление секций в резюме
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM section WHERE resume_uuid =?")) {
                    preparedStatement.setString(1, uuid);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            String content = resultSet.getString("content");
                            SectionType type = SectionType.valueOf(resultSet.getString("type")); // преобразование строки в поле type в json в объект Enum (SectionType.EXPERIENCE)
                            if (content != null) {
                                Section section = JsonParser.read(content, Section.class);
                                resume.addSection(type, section);
                            }
                        }
                    }
                }

                // Коммит транзакции, если все успешно
                connection.commit();
            } catch (SQLException e) { // если возникло одно из следующих исключений:
                // connection.commit(), connection.prepareStatement(), connection.executeQuery(),
                // то вызывается connection.rollback() для отката транзакции
                try {
                    connection.rollback(); // откат транзакции
                } catch (SQLException ex) { // Если возникло исключение connection.rollback()
                    throw new RuntimeException(ex);
                }
            }
        } catch (SQLException ex) { // Если возникло исключение DriverManager.getConnection
            throw new RuntimeException(ex);
        }
        return resume;
    }

    //TODO добавить sqlhelper

    public void save(Resume r) {
        try (FileInputStream fileInputStream = new FileInputStream("WEB-INF/config/resumes.properties")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (Connection conn = DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password"))) {
            conn.setAutoCommit(false); // начинаем транзакцию
            try (
                    PreparedStatement psResume = conn.prepareStatement("INSERT INTO resume (uuid, full_name) VALUES (?, ?)");
                    PreparedStatement psContact = conn.prepareStatement("INSERT INTO contact (resume_uuid, type, value) VALUES (?, ?, ?)");
                    PreparedStatement psSection = conn.prepareStatement("INSERT INTO section (resume_uuid, type, content) VALUES (?, ?, ?)")
            ) {
                // 1. Сохраняем резюме
                psResume.setString(1, r.getUuid());
                psResume.setString(2, r.getFullName());
                psResume.executeUpdate();

                // 2. Сохраняем контакты
                for (Map.Entry<ContactType, String> entry : r.getContacts().entrySet()) {
                    psContact.setString(1, r.getUuid());
                    psContact.setString(2, entry.getKey().name());
                    psContact.setString(3, entry.getValue());
                    psContact.addBatch();
                }
                psContact.executeBatch();

                // 3. Сохраняем секции
                for (Map.Entry<SectionType, Section> entry : r.getSections().entrySet()) {
                    psSection.setString(1, r.getUuid());
                    psSection.setString(2, entry.getKey().name());
                    psSection.setString(3, JsonParser.write(entry.getValue(), Section.class)); // сериализуем через JsonParser
                    psSection.addBatch();
                }
                psSection.executeBatch();
                conn.commit(); // всё прошло успешно — коммитим
            } catch (Exception e) {
                conn.rollback(); // в случае ошибки — откат
                throw new StorageException("Ошибка при сохранении резюме " + r.getUuid(), r.getUuid(), e);
            }
        } catch (SQLException e) {
            throw new StorageException("Ошибка подключения к БД", e);
        }
    }

    @Override
    public List<Resume> getAllSorted() {
        Map<String, Resume> resumeMap = new LinkedHashMap<>();
        // Теперь путь передается в конструктор, используем его для загрузки конфигурации
        try (FileInputStream fileInputStream = new FileInputStream(configFilePath)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки файла конфигурации", e);
        }

        try (Connection conn = DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password"))) {
            conn.setAutoCommit(false);

            // 1. Получаем все резюме
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM resume ORDER BY full_name, uuid")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String uuid = rs.getString("uuid");
                        String fullName = rs.getString("full_name");
                        resumeMap.put(uuid, new Resume(uuid, fullName));
                    }
                }
            }

            // 2. Добавляем контакты
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM contact")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Resume resume = resumeMap.get(rs.getString("resume_uuid"));
                        if (resume != null) {
                            ContactType type = ContactType.valueOf(rs.getString("type"));
                            String value = rs.getString("value");
                            resume.addContact(type, value);
                        }
                    }
                }
            }

            // 3. Добавляем секции
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM section")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Resume resume = resumeMap.get(rs.getString("resume_uuid"));
                        if (resume != null) {
                            SectionType type = SectionType.valueOf(rs.getString("type"));
                            String content = rs.getString("content");
                            resume.addSection(type, JsonParser.read(content, Section.class));
                        }
                    }
                }
            }

            conn.commit();
            return resumeMap.values().stream().toList();

        } catch (SQLException e) {
            throw new StorageException("Ошибка при получении всех резюме", e);
        }
    }

    @Override
    public void delete(String uuid) {
        try (FileInputStream fileInputStream = new FileInputStream("WEB-INF/config/resumes.properties")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Connection conn = DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password"));
             PreparedStatement ps = conn.prepareStatement("DELETE FROM resume WHERE uuid = ?")) {
            ps.setString(1, uuid);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new NotExistStorageException(uuid);
            }
        } catch (SQLException e) {
            throw new StorageException("Ошибка при удалении резюме", uuid, e);
        }
    }

    @Override
    public int size() {
        try (FileInputStream fileInputStream = new FileInputStream("WEB-INF/config/resumes.properties")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Connection conn = DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password"));
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM resume")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new StorageException("Ошибка при подсчете резюме");
        } catch (SQLException e) {
            throw new StorageException("Ошибка при подсчете резюме", e);
        }
    }

    @Override
    public void update(Resume r) {
        try (FileInputStream fileInputStream = new FileInputStream("WEB-INF/config/resumes.properties")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Connection conn = DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password"))) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement("UPDATE resume SET full_name = ? WHERE uuid = ?")) {
                ps.setString(1, r.getFullName());
                ps.setString(2, r.getUuid());

                int affected = ps.executeUpdate();
                if (affected == 0) {
                    throw new NotExistStorageException(r.getUuid());
                }

                try (PreparedStatement psDeleteContact = conn.prepareStatement("DELETE FROM contact WHERE resume_uuid = ?");
                     PreparedStatement psDeleteSection = conn.prepareStatement("DELETE FROM section WHERE resume_uuid = ?")) {
                    psDeleteContact.setString(1, r.getUuid());
                    psDeleteContact.executeUpdate();
                    psDeleteSection.setString(1, r.getUuid());
                    psDeleteSection.executeUpdate();
                }

                try (PreparedStatement psContact = conn.prepareStatement("INSERT INTO contact (resume_uuid, type, value) VALUES (?, ?, ?)");
                     PreparedStatement psSection = conn.prepareStatement("INSERT INTO section (resume_uuid, type, content) VALUES (?, ?, ?)")) {
                    for (Map.Entry<ContactType, String> entry : r.getContacts().entrySet()) {
                        psContact.setString(1, r.getUuid());
                        psContact.setString(2, entry.getKey().name());
                        psContact.setString(3, entry.getValue());
                        psContact.addBatch();
                    }
                    psContact.executeBatch();

                    for (Map.Entry<SectionType, Section> entry : r.getSections().entrySet()) {
                        psSection.setString(1, r.getUuid());
                        psSection.setString(2, entry.getKey().name());
                        psSection.setString(3, JsonParser.write(entry.getValue(), Section.class));
                        psSection.addBatch();
                    }
                    psSection.executeBatch();
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new StorageException("Ошибка при обновлении резюме " + r.getUuid(), r.getUuid(), e);
            }
        } catch (SQLException e) {
            throw new StorageException("Ошибка подключения к БД", e);
        }
    }

    @Override
    public void clear() {
        try (FileInputStream fileInputStream = new FileInputStream("WEB-INF/config/resumes.properties")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Connection conn = DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password"));
             Statement st = conn.createStatement()) {
            st.execute("DELETE FROM contact");
            st.execute("DELETE FROM section");
            st.execute("DELETE FROM resume");
        } catch (SQLException e) {
            throw new StorageException("Ошибка при очистке базы данных", e);
        }
    }
}
