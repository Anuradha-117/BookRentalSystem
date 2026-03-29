# 📚 Book Nest - Book Rental System (JavaFX)

A comprehensive standalone application for managing a book rental shop. This system allows Admins and Staff to manage books, customers, rentals, and returns with automatic fine calculation.

## 🚀 Key Features
* **User Management:** Secure login with role-based access (Super Admin, Admin, Staff).
* **Dashboard:** Real-time metrics for overdue books and active rentals.
* **Rentals & Returns:** Issue books and calculate fines for late returns automatically.
* **Inventory Control:** Add, update, and delete books.
* **Security:** * Admins cannot delete the main system owner.
  * Privilege escalation protection (Admins cannot promote staff to Admin).

## 🛠 Technologies Used
* **Language:** Java (JDK 11)
* **UI Framework:** JavaFX
* **Database:** MySQL (Local / Auto-Initialized via properties file)
* **Architecture:** MVC (Model-View-Controller)

## 🔑 Login Credentials (For Testing)
Use these accounts to test the different access levels:

| Role | Username | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **Super Admin** | `admin` | `1234` | Full Access (Manage Users, Delete Books) |
| **Admin** | *(Create manually)* | *(Create manually)* | Can create or update staff and manage shop functionalities |
| **Staff** | *(Create manually)* | *(Create manually)* | Limited Access (Rentals & Returns only) |

*(Note: The system creates the 'Super admin' user automatically on the first run. You can create Staff or Admin users after logging in as Super Admin).*

---

## 📥 Quick Start (Easiest Way to Test)
This application features an **Automated Database Initialization** system. You do not need to manually execute any SQL scripts to test the `.exe`!

1. Go to the **[Releases](../../releases)** section on the right side of this GitHub page.
2. Download both the `BookNest.exe` file and the `database.properties.template` file.
3. Place both files in the **same folder** on your computer.
4. Rename `database.properties.template` to `database.properties`.
5. Open the file in Notepad and enter your local MySQL **username** and **password** (leave the password blank if you use XAMPP's default settings).
6. Double-click `BookNest.exe` to run. The system will automatically build the database and tables!

---

## ⚙️ Developer Setup (Running Locally from Source)
If you wish to compile the code yourself in your IDE, follow these steps:

### 1. Configure Connection
1. Open the project in your IDE (IntelliJ IDEA).
2. Ensure you have a `database.properties` file in your project's root directory.
3. Update the `db.url=jdbc:mysql://localhost:3306/`, `db.username`, and `db.password` fields to match your local MySQL credentials.

### 2. Run the Application
1. Navigate to `src/main/java/edu/icet/Main.java`.
2. Right-click the file and select **Run 'Main'**.

---

## 🛠️ Manual Database Setup (Fallback)
If the automated setup fails due to system permissions, you can set the database up manually:

### 1. Database Setup
The project includes a file named `database_setup.sql` containing the necessary SQL commands.
1. Open your **MySQL Workbench** or **MySQL CLI**.
2. Open the `database_setup.sql` file from this repository.
3. Copy and paste the commands into your MySQL tool and **execute them**.

### 2. Update Java Code (Bypass Properties File)
If you manually created the database, you no longer need the automated setup code. Navigate to `src/main/java/edu/icet/db/DBConnection.java` and replace the **entire contents** of the file with this simplified, hardcoded version:

```java
package edu.icet.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection dbConnection;
    private Connection connection;

    private DBConnection() throws SQLException {
        try {
            // NOTE: Change "YOUR_LOCAL_PASSWORD" to your actual MySQL password
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/book_rental_db", "root", "YOUR_LOCAL_PASSWORD");
        } catch (SQLException ex) {
            System.out.println("Database connection failed! Ensure MySQL is running and credentials are correct.");
            ex.printStackTrace();
            throw new SQLException("Could not connect to the database.", ex);
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
```

---
*Developed by Anuradha Lakruwan*
