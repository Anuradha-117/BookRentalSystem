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
* **Database:** MySQL (Cloud-Hosted via db4free / freesqldatabase)
* **Architecture:** MVC (Model-View-Controller)

## 🔑 Login Credentials (For Testing)
Use these accounts to test the different access levels:

| Role | Username | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **Super Admin** | `admin` | `1234` | Full Access (Manage Users, Delete Books) |
| **Admin** | *(Create manually)* | *(Create manually)* | Can create or update staff and manage shop functionalities |
| **Staff** | *(Create manually)* | *(Create manually)* | Limited Access (Rentals & Returns only) |

*(Note: The database script creates the 'Super admin' user automatically. You can create Staff or Admin users after logging in as Super Admin).*

---

## 📥 Quick Start (Easiest Way to Test)
This application is pre-configured to connect to a **remote cloud MySQL database**. You do not need to set up a local database to test it!

1. Go to the **[Releases](../../releases)** section on the right side of this GitHub page.
2. Download the `BookNest.exe` file.
3. Double-click to run. Log in using the Super Admin credentials above!

---

## ⚙️ Developer Setup (Running Locally from Source)
If you wish to compile the code yourself or use a local database instead of the cloud database, follow these steps:

### 1. Database Setup
The project includes a file named `database_setup.sql` containing the necessary SQL commands.
1. Open your **MySQL Workbench** or **MySQL CLI**.
2. Open the `database_setup.sql` file from this repository.
3. Copy and paste the commands into your MySQL tool and **execute them**.

### 2. Configure Connection
1. Open the project in your IDE (IntelliJ IDEA / Eclipse).
2. Navigate to `src/main/java/edu/icet/db/DBConnection.java`.
3. Update the connection string, `user`, and `password` fields to match your local MySQL credentials instead of the cloud credentials.

### 3. Run the Application
1. Navigate to `src/main/java/edu/icet/Main.java`.
2. Right-click the file and select **Run 'Main'**.

---
*Developed by Anuradha Lakruwan*
