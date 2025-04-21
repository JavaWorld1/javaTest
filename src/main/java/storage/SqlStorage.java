package storage;

import exception.NotExistStorageException;
import model.ContactType;
import model.Resume;
import model.Section;
import model.SectionType;
import util.JsonParser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlStorage implements Storage {
    private final SqlHelper sqlHelper;

    public SqlStorage() {
        this.sqlHelper = new SqlHelper();
    }

    @Override
    public void save(Resume r) {
        sqlHelper.executeTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO resume (uuid, full_name) VALUES (?, ?)")) {
                ps.setString(1, r.getUuid());
                ps.setString(2, r.getFullName());
                ps.executeUpdate();
            }

            insertContacts(conn, r);
            insertSections(conn, r);
            return null;
        });
    }

    @Override
    public Resume get(String uuid) {
        return sqlHelper.execute(new SqlTransaction<Resume>() {
            @Override
            public Resume execute(Connection conn) throws SQLException {
                Resume resume;
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM resume WHERE uuid = ?")) {
                    ps.setString(1, uuid);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        throw new NotExistStorageException(uuid);
                    }
                    resume = new Resume(uuid, rs.getString("full_name"));
                }

                SqlStorage.this.loadContacts(conn, resume);
                SqlStorage.this.loadSections(conn, resume);
                return resume;
            }
        });
    }


    @Override
    public void delete(String uuid) {
        sqlHelper.execute(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM resume WHERE uuid = ?")) {
                ps.setString(1, uuid);
                if (ps.executeUpdate() == 0) {
                    throw new NotExistStorageException(uuid);
                }
                return null;
            }
        });
    }

    @Override
    public void update(Resume r) {
        sqlHelper.executeTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE resume SET full_name = ? WHERE uuid = ?")) {
                ps.setString(1, r.getFullName());
                ps.setString(2, r.getUuid());
                if (ps.executeUpdate() == 0) {
                    throw new NotExistStorageException(r.getUuid());
                }
            }

            deleteContacts(conn, r.getUuid());
            deleteSections(conn, r.getUuid());
            insertContacts(conn, r);
            insertSections(conn, r);
            return null;
        });
    }

    @Override
    public List<Resume> getAllSorted() {
        return sqlHelper.execute(conn -> {
            List<Resume> resumes = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM resume ORDER BY full_name")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    resumes.add(new Resume(rs.getString("uuid"), rs.getString("full_name")));
                }
            }

            for (Resume r : resumes) {
                loadContacts(conn, r);
                loadSections(conn, r);
            }

            return resumes;
        });
    }

    @Override
    public void clear() {
        sqlHelper.executeTransaction(conn -> {
            // Очищаем таблицу contact
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM contact")) {
                ps.executeUpdate();
            }

            // Очищаем таблицу section
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM section")) {
                ps.executeUpdate();
            }

            // Очищаем таблицу resume
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM resume")) {
                ps.executeUpdate();
            }

            return null;
        });
    }


    @Override
    public int size() {
        return sqlHelper.execute(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM resume")) {
                ResultSet rs = ps.executeQuery();
                return rs.next() ? rs.getInt(1) : 0;
            }
        });
    }

    private void insertContacts(Connection conn, Resume r) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO contact (resume_uuid, type, value) VALUES (?, ?, ?)")) {
            for (Map.Entry<ContactType, String> entry : r.getContacts().entrySet()) {
                ps.setString(1, r.getUuid());
                ps.setString(2, entry.getKey().name());
                ps.setString(3, entry.getValue());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertSections(Connection conn, Resume r) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO section (resume_uuid, type, content) VALUES (?, ?, ?)")) {
            for (Map.Entry<SectionType, Section> entry : r.getSections().entrySet()) {
                ps.setString(1, r.getUuid());
                ps.setString(2, entry.getKey().name());
                ps.setString(3, JsonParser.write(entry.getValue(), Section.class));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void loadContacts(Connection conn, Resume r) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM contact WHERE resume_uuid = ?")) {
            ps.setString(1, r.getUuid());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ContactType type = ContactType.valueOf(rs.getString("type"));
                String value = rs.getString("value");
                r.addContact(type, value);
            }
        }
    }

    private void loadSections(Connection conn, Resume r) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM section WHERE resume_uuid = ?")) {
            ps.setString(1, r.getUuid());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SectionType type = SectionType.valueOf(rs.getString("type"));
                Section section = JsonParser.read(rs.getString("content"), Section.class);
                r.addSection(type, section);
            }
        }
    }

    private void deleteContacts(Connection conn, String uuid) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM contact WHERE resume_uuid = ?")) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        }
    }

    private void deleteSections(Connection conn, String uuid) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM section WHERE resume_uuid = ?")) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        }
    }
}
