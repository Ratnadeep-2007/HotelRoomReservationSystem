# Hotel Room Reservation System

This project has two applications on the same MySQL database (`hotel_db`):

1. **Web application** - Servlets + JSP + JDBC (for Tomcat).
2. **Desktop application** - Swing GUI + JDBC (`desktop/` folder).

## Web flow

1. Open the home page (`index.jsp`) and enter check-in and check-out dates.
2. View available rooms and book with guest name and phone.
3. View reservations and cancel a booking.
4. Use **Manage Rooms (CRUD)** to add, edit and delete rooms.
5. Use **Save Guest** on the home page to store a Guest object in the session.

Total amount = price per night multiplied by number of nights.
Cancelled bookings stay in the list, but no longer block the room.

## Topics used

- **OOPs Concept**: `Person` -> `Guest` inheritance, `Room` model class (encapsulation).
- **CRUD Operations**: `RoomDao` (add, read, update, delete) used by web and desktop apps.
- **ResultSet + Java List**: query rows are read from a `ResultSet` into `List<Room>`.
- **Lambda Expressions**: sorting the room list and button listeners in the desktop app.
- **JDBC with Servlets**: servlets call `RoomDao`, which runs JDBC queries.
- **JSP**: `index.jsp`, `rooms.jsp`, `room-form.jsp`, `guest.jsp`.
- **Passing Data between JSP Pages**: servlet sets a request attribute and forwards to a JSP; the guest is passed through the session.
- **Session Management with objects**: the `Guest` object is stored with `session.setAttribute("guest", guest)`.
- **Setting Attributes**: request scope (`rooms`, `room`), session scope (`message`, `guest`) and application scope (`roomPageVisits`).

## Setup

1. Run `database/hotel_db.sql` in MySQL Workbench.
2. Set your MySQL password in `src/main/java/com/hotel/config/DBConnect.java`
   and in `desktop/src/com/hotel/desktop/DBConnect.java`.
3. Double-click `start.bat` and choose:
   - `1` to build the web WAR (`target/HotelRoomReservationSystem.war`), or
   - `2` to run the desktop application.
4. For the web app, deploy the WAR in Tomcat 10.1 (or Run from NetBeans),
   then open `http://localhost:8080/HotelRoomReservationSystem/`.

## Main files

- `index.jsp`: home page, date search and guest session form.
- `RoomListServlet.java`: available rooms for the selected dates.
- `ReservationServlet.java`: checks availability and saves a booking.
- `ReservationsListServlet.java`: displays reservations.
- `CancelReservationServlet.java`: cancels a booking.
- `RoomCrudServlet.java` + `RoomDao.java`: room CRUD for `rooms.jsp`.
- `GuestServlet.java` + `guest.jsp`: Guest object in the session.
- `desktop/src/com/hotel/desktop/HotelDesktopApp.java`: Swing GUI (table + Add/Update/Delete).
- `start.bat`: build the web app or run the desktop app.
- `database/hotel_db.sql`: tables and five sample rooms.

The booking transaction locks the room while checking and saving, so two bookings cannot take the same dates.
