CREATE DATABASE IF NOT EXISTS hotel_db;
USE hotel_db;

CREATE TABLE IF NOT EXISTS rooms (
  room_id INT PRIMARY KEY AUTO_INCREMENT,
  room_number VARCHAR(10) NOT NULL UNIQUE,
  room_type VARCHAR(30) NOT NULL,
  price_per_night DECIMAL(10,2) NOT NULL,
  capacity INT NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS reservations (
  reservation_id INT PRIMARY KEY AUTO_INCREMENT,
  guest_name VARCHAR(100) NOT NULL,
  phone VARCHAR(25) NOT NULL,
  room_id INT NOT NULL,
  check_in DATE NOT NULL,
  check_out DATE NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  booking_status VARCHAR(20) NOT NULL DEFAULT 'Booked',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_reservation_room FOREIGN KEY (room_id) REFERENCES rooms(room_id),
  CONSTRAINT chk_reservation_dates CHECK (check_out > check_in)
);

INSERT IGNORE INTO rooms (room_number, room_type, price_per_night, capacity) VALUES
('101','Single',1500.00,1),
('102','Double',2500.00,2),
('103','Deluxe',4000.00,3),
('104','Suite',6000.00,4),
('105','Double',2500.00,2);
