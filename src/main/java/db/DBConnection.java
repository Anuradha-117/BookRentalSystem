package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/book_rental_db", "root", "2424");
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}