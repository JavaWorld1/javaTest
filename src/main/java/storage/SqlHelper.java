package storage;

import exception.StorageException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SqlHelper {
    private final String url;
    private final String user;
    private final String password;

    public SqlHelper() {
        try {
            Properties props = new Properties();
            try (InputStream is = SqlHelper.class.getClassLoader().getResourceAsStream("resumes.properties")) {
                if (is == null) {
                    throw new StorageException("Configuration file not found");
                }
                props.load(is);
            }
            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");

            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
        } catch (IOException | ClassNotFoundException e) {
            throw new StorageException("Ошибка при загрузке настроек БД", e);
        }
    }

    public <T> T execute(SqlTransaction<T> executor) {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            return executor.execute(conn);
        } catch (SQLException e) {
            throw new StorageException("Database connection error", e);
        }
    }

    public <T> T executeTransaction(SqlTransaction<T> executor) {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try {
                conn.setAutoCommit(false);
                T result = executor.execute(conn);
                conn.commit();
                return result;
            } catch (SQLException e) {
                conn.rollback();
                throw new StorageException("Transaction failed", e);
            }
        } catch (SQLException e) {
            throw new StorageException("Database connection error", e);
        }
    }
}