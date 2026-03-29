package edu.icet.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBConnection {
    private static DBConnection dbConnection;
    private Connection connection;

    private DBConnection() throws SQLException {
        Properties props = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new SQLException("Unable to find database.properties file.");
            }

            props.load(input);
            String dbUrl = props.getProperty("db.url");
            String dbUser = props.getProperty("db.username");
            String dbPass = props.getProperty("db.password");

            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            initDatabase();

        } catch (Exception ex) {
            System.out.println("Database connection or initialization failed!");
            ex.printStackTrace();
            throw new SQLException("Could not connect to or setup the database.", ex);
        }
    }

    private void initDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS book_rental_db");
            stmt.execute("USE book_rental_db");

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "password VARCHAR(50) NOT NULL, " +
                    "role VARCHAR(20) NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS books (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(100) NOT NULL, " +
                    "author VARCHAR(100) NOT NULL, " +
                    "category VARCHAR(50) NOT NULL, " +
                    "quantity INT NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS customers (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "phone VARCHAR(20) NOT NULL UNIQUE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS rentals (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "book_id INT NOT NULL, " +
                    "customer_id INT NOT NULL, " +
                    "issue_date DATE NOT NULL, " +
                    "return_date DATE, " +
                    "fine DOUBLE DEFAULT 0.0, " +
                    "FOREIGN KEY (book_id) REFERENCES books(id), " +
                    "FOREIGN KEY (customer_id) REFERENCES customers(id))");

            stmt.execute("INSERT IGNORE INTO users (username, password, role) VALUES ('admin', '1234', 'Admin')");
        }
    }

    public static DBConnection getInstance() throws SQLException {
        if (dbConnection == null) {
            dbConnection = new DBConnection();
        }
        return dbConnection;
    }

    public Connection getConnection() {
        return connection;
    }
}