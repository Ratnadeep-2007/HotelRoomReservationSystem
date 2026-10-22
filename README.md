# Hotel Room Reservation System

A simple Java web project using Servlets, JDBC and MySQL.

## Flow

1. Enter check-in and check-out dates on the home page.
2. View available rooms.
3. Enter guest name and phone number to book a room.
4. View reservations and cancel a booking.

Total amount = price per night multiplied by number of nights.
Cancelled bookings stay in the list, but no longer block the room.

## Setup

1. Run `database/hotel_db.sql` in MySQL Workbench.
2. Set your MySQL username and password in `src/main/java/com/hotel/config/DBConnect.java`.
3. Open the project in NetBeans using JDK 17 or newer.
4. Add Apache Tomcat 10.1 and select it as the project server.
5. Clean and Build, then Run the project.

Maven creates `target/HotelRoomReservationSystem.war`.
When deployed under that name, open `http://localhost:8080/HotelRoomReservationSystem/`.
NetBeans may use a different context path; use the URL it opens.

## Files

- `index.html`: date search form.
- `RoomListServlet.java`: displays available rooms and booking forms.
- `ReservationServlet.java`: checks availability and saves a booking.
- `ReservationsListServlet.java`: displays reservations.
- `CancelReservationServlet.java`: cancels a booking.
- `DBConnect.java`: connects to MySQL.
- `Html.java`: makes text safe to display in HTML.
- `database/hotel_db.sql`: tables and five sample rooms.

The pages use plain HTML. Each servlet directly runs its SQL queries.
The booking transaction locks the room while checking and saving, so two bookings cannot take the same dates.
