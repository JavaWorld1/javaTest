package com.webapp.storage;

import com.webapp.exception.NotExistStorageException;
import com.webapp.exception.StorageException;
import com.webapp.model.ContactType;
import com.webapp.model.Resume;
import com.webapp.model.SectionType;
import com.webapp.util.JsonParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SqlStorage {
    public Resume get(String uuid) throws NotExistStorageException {

        Resume resume = null;
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("config/resumes.properties")) {
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

                // добавление контаков пользователя в резюме
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

                //TODO добавление секций в резюме
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM section WHERE resume_uuid =?")) {
                    preparedStatement.setString(1, uuid);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (!resultSet.next()) { // resultSet.next() вернет false, что будет означать, что в результате запроса нет строк.
                            // Однако, в этом случае мы сразу бросаем исключение NotExistStorageException,
                            // не пытаясь читать данные из результата запроса, поэтому ошибки не будет. Нельзя работать с пустыми строками.
                            throw new NotExistStorageException(uuid);
                        }
                        while (resultSet.next()) {
                            String content = resultSet.getString("content");
                            SectionType type = SectionType.valueOf(resultSet.getString("type"));
                            if (content != null) {
                                resume.addSection(type, JsonParser.read(content)); // перевод строки content из базы  в объект Section.
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

    public void save(Resume resume) throws StorageException {

    }

    // вставка контактов resume в таблицу contact
    private void insertContacts(Connection connection, Resume resume) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO contact (resume_uuid, type, value) VALUES (?, ?, ?)")) {
            for (Map.Entry<ContactType, String> contactType : resume.getContacts().entrySet()) {
                preparedStatement.setString(1, resume.getUuid()); // uuid резюме
                preparedStatement.setString(2, contactType.getKey().name()); // тип контакта
                preparedStatement.setString(3, contactType.getValue()); // значение контакта в виде строки
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new StorageException(e); // Если возникло исключение SQLException
        }
    }

    public List<Resume> getAllSorted() {
        Map<String, Resume> resumesMap = new LinkedHashMap<>();
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("config/resumes.properties")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password"));
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM resume ORDER BY full_name, uuid")) {

            // Начало транзакции
            // устанавливает ручное управление транзакцией, и после успешного выполнения операций метода,
            // вызывается connection.commit() для фиксации изменений. В случае возникновения исключения,
            // вызывается connection.rollback() для отката транзакции.
            connection.setAutoCommit(false);


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String uuid = resultSet.getString("resume_uuid");
                    resumesMap.put(uuid, new Resume(uuid, resultSet.getString("full_name")));
                }


                return null;
            } catch (SQLException e) {
                // Откат транзакции в случае ошибки
                connection.rollback();
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String uuid) throws NotExistStorageException {

    }

    public void clear() {
//        ("DELETE FROM resume");
    }


}
