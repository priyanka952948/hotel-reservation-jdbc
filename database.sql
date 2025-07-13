CREATE DATABASE IF NOT EXISTS hotel2_db;
USE hotel2_db;

CREATE TABLE IF NOT EXISTS reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    guest_name VARCHAR(100),
    contact_number VARCHAR(15),
    room_number INT,
    reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);