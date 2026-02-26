CREATE DATABASE IF NOT EXISTS book_rental_db;
USE book_rental_db;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    quantity INT NOT NULL
);

CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE rentals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    customer_id INT NOT NULL,
    issue_date DATE NOT NULL,
    return_date DATE,
    fine DOUBLE DEFAULT 0.0,
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

INSERT INTO users (username, password, role) VALUES ('admin', '1234', 'Admin');