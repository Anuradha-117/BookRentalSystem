# 📚 Book Rental System (JavaFX)

A comprehensive standalone application for managing a book rental shop. This system allows Admins and Staff to manage books, customers, rentals, and returns with automatic fine calculation.

## 🚀 Key Features
* **User Management:** Secure login with role-based access (Admin vs. Staff).
* **Dashboard:** Real-time metrics for overdue books and active rentals.
* **Rentals & Returns:** Issue books and calculate fines for late returns automatically.
* **Inventory Control:** Add, update, and delete books.
* **Security:** * Admins cannot delete the main system owner.
    * Privilege escalation protection (Admins cannot promote staff to Admin).

## 🛠 Technologies Used
* **Language:** Java (JDK 8/11)
* **UI Framework:** JavaFX
* **Database:** MySQL
* **Architecture:** MVC (Model-View-Controller)

## 🔑 Login Credentials (For Testing)
Use these accounts to test the different access levels:

| Role | Username | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **Super Admin** | `admin` | `1234` | Full Access (Manage Users, Delete Books) |
| **Staff** | *(Create manually)* | *(Create manually)* | Limited Access (Rentals & Returns only) |
| **Admin** | *(Create manually)* | *(Create manually)* | Can create or update staff and bookshop functionalities |

*(Note: The database script creates the 'Super admin' user automatically. You can create Staff or Admin users after logging in as Super Admin).*

## ⚙️ Setup Instructions

### 1. Database Setup
The project includes a file named `database_setup.sql` containing the necessary SQL commands.
1.  Open your **MySQL Workbench** or **MySQL CLI**.
2.  Open the `database_setup.sql` file from this repository.
3.  Copy and paste the commands into your MySQL tool and **execute them**.
    * This will create the database `book_rental_db`, set up the tables (`users`, `books`, `customers`, `rentals`), and insert the default Admin account.

### 2. Configure Connection
1.  Open the project in your IDE (IntelliJ IDEA / Eclipse).
2.  Navigate to `src/main/java/db/DBConnection.java`.
3.  Update the `user` and `password` fields to match your local MySQL credentials.

### 3. Run the Application
1.  Navigate to `src/main/java/Main.java`.
2.  Right-click the file and select **Run 'Main'**.
3.  The login screen should appear.

---
*Developed by Anuradha lakruwan*
