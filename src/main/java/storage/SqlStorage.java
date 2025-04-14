package storage;

import exception.NotExistStorageException;
import exception.StorageException;
import util.JsonParser;
import model.ContactType;
import model.Resume;
import model.Section;
import model.SectionType;

import java.io.InputStream;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SqlStorage implements Storage {

    private final Connection connection;

    public SqlStorage(InputStream input) {
        try {
            Properties props = new Properties();
            props.load(input);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new StorageException("Ошибка подключения к базе данных", e);
        }
    }

    public Resume get(String uuid) throws NotExistStorageException {
        Resume resume = null;
        try {
            connection.setAutoCommit(false); // устанавливает ручное управление коммитом транзакции
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
            try {
                connection.rollback(); // откат транзакции
            } catch (SQLException ex) { // Если возникло исключение connection.rollback()
                throw new RuntimeException(ex);
            }
        }
        return resume;
    }

    //TODO добавить sqlhelper

    public void save(Resume r) {
        try {
            connection.setAutoCommit(false); // начинаем транзакцию
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        try (
                PreparedStatement psResume = connection.prepareStatement("INSERT INTO resume (uuid, full_name) VALUES (?, ?)");
                PreparedStatement psContact = connection.prepareStatement("INSERT INTO contact (resume_uuid, type, value) VALUES (?, ?, ?)");
                PreparedStatement psSection = connection.prepareStatement("INSERT INTO section (resume_uuid, type, content) VALUES (?, ?, ?)")
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
            connection.commit(); // всё прошло успешно — коммитим
        } catch (Exception e) {
            try {
                connection.rollback(); // в случае ошибки — откат
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new StorageException("Ошибка при сохранении резюме " + r.getUuid(), r.getUuid(), e);
        }
    }

    @Override
    public List<Resume> getAllSorted() {
        Map<String, Resume> resumeMap = new LinkedHashMap<>();
        // Теперь путь передается в конструктор, используем его для загрузки конфигурации

        try {
            connection.setAutoCommit(false);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        // 1. Получаем все резюме
            try (
                    PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM resume ORDER BY full_name, uuid")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String uuid = rs.getString("uuid");
                        String fullName = rs.getString("full_name");
                        resumeMap.put(uuid, new Resume(uuid, fullName));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        // 2. Добавляем контакты
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM contact")) {
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
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        // 3. Добавляем секции
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM section")) {
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
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        try {
            connection.commit();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return resumeMap.values().stream().toList();
    }

    @Override
    public void delete(String uuid) {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM resume WHERE uuid = ?")) {
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
        try (Statement st = connection.createStatement();
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
        try (PreparedStatement ps = connection.prepareStatement("UPDATE resume SET full_name = ? WHERE uuid = ?")) {
            ps.setString(1, r.getFullName());
            ps.setString(2, r.getUuid());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new NotExistStorageException(r.getUuid());
            }

            try (PreparedStatement psDeleteContact = connection.prepareStatement("DELETE FROM contact WHERE resume_uuid = ?");
                 PreparedStatement psDeleteSection = connection.prepareStatement("DELETE FROM section WHERE resume_uuid = ?")) {
                psDeleteContact.setString(1, r.getUuid());
                psDeleteContact.executeUpdate();
                psDeleteSection.setString(1, r.getUuid());
                psDeleteSection.executeUpdate();
            }

            try (PreparedStatement psContact = connection.prepareStatement("INSERT INTO contact (resume_uuid, type, value) VALUES (?, ?, ?)");
                 PreparedStatement psSection = connection.prepareStatement("INSERT INTO section (resume_uuid, type, content) VALUES (?, ?, ?)")) {
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

            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new StorageException("Ошибка при обновлении резюме " + r.getUuid(), r.getUuid(), e);
        }
    }

    @Override
    public void clear() {
        try (Statement st = connection.createStatement()) {
            st.execute("DELETE FROM contact");
            st.execute("DELETE FROM section");
            st.execute("DELETE FROM resume");
        } catch (SQLException e) {
            throw new StorageException("Ошибка при очистке базы данных", e);
        }
    }
}
