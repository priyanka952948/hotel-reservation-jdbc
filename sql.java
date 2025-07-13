create database hotel2_db;
USE hotel2_db;

CREATE TABLE IF NOT EXISTS reservations (
        reservation_id INT PRIMARY KEY AUTO_INCREMENT,
        guest_name VARCHAR(100) NOT NULL,
room_number INT NOT NULL,
contact_number VARCHAR(15) NOT NULL,
reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

