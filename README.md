# Hotel Room Reservation System

A web-based Hotel Room Reservation System developed using **Java Servlets**, **JSP**, **JDBC**, and **MySQL**, configured as an **Apache Ant Web Application** compatible with **Apache NetBeans** and **Apache Tomcat 10+ (Jakarta EE)**.

---

## Features
- **Browse Rooms:** View all available rooms, room types, capacities, and pricing.
- **Room Management (CRUD):** Add, update, and manage room inventory with validation.
- **Guest Booking:** Interactive booking flow with date selection, guest details, and automated price calculation.
- **Reservation Records:** View all reservations with guest contact details and room assignments.
- **Reservation Cancellation:** Cancel bookings with real-time status updates in MySQL.

---

## Tech Stack
- **Backend:** Java 17+, Jakarta Servlet 6.0 (`jakarta.servlet.*`), JDBC
- **Database:** MySQL (`hotel_db`) via `mysql-connector-j`
- **Frontend / Templating:** JSP, HTML5, CSS3
- **Server:** Apache Tomcat 10+ (Jakarta EE 10)
- **Build System:** Apache Ant (Native NetBeans Web Application)

---

## Project Structure
```text
HotelRoomReservationSystem/
├── database/
│   └── hotel_db.sql               # MySQL database schema & sample data
├── nbproject/                     # NetBeans Ant project configuration
│   ├── ant-deploy.xml
│   ├── build-impl.xml
│   ├── genfiles.properties
│   ├── project.properties
│   └── project.xml
├── src/
│   └── java/
│       └── hotel/                 # Exactly 4 Clean Java Files for College Practical
│           ├── HotelDao.java      # DB Connection (JDBC), PreparedStatement CRUD, Lambda & Custom Exception
│           ├── HotelServlet.java  # Master Front Controller Servlet (/rooms, /room-crud, /reserve, /cancel, /guest)
│           ├── Person.java        # OOP Inheritance (Person & Guest extends Person) + Session Bean
│           └── Room.java          # Room Entity Model Bean
├── web/
│   ├── META-INF/
│   │   └── context.xml
│   ├── WEB-INF/
│   │   ├── lib/
│   │   │   └── mysql-connector-j-8.3.0.jar
│   │   └── web.xml
│   ├── available-rooms.jsp
│   ├── guest.jsp
│   ├── index.html
│   ├── index.jsp
│   ├── reservations.jsp
│   ├── room-form.jsp
│   └── rooms.jsp
├── build.xml                      # Ant build file
├── .gitignore
└── README.md
```

---

## Setup & Running in NetBeans

### 1. Database Setup
1. Start your **MySQL Server** (via MySQL Service or XAMPP).
2. Open MySQL Workbench, phpMyAdmin, or the MySQL command line.
3. Execute the script in [`database/hotel_db.sql`](database/hotel_db.sql) to create `hotel_db` and pre-populate sample rooms:
   ```sql
   source database/hotel_db.sql;
   ```
4. Verify your credentials in `src/java/com/hotel/config/DBConnect.java` (default user: `root`, password: `""`).

### 2. Open & Run in NetBeans
1. Open **Apache NetBeans**.
2. Go to **File** $\rightarrow$ **Open Project...** (`Ctrl + Shift + O`).
3. Select this folder (`HotelRoomReservationSystem`).
4. Right-click the project $\rightarrow$ **Run** (or press **F6**).
5. NetBeans will build using Ant, deploy to Tomcat 10, and open:
   ```
   http://localhost:8080/HotelReservation/
   ```

---

## Accessing from Another Device on Local Network
1. Ensure both devices are connected to the same Wi-Fi / LAN network.
2. Find the host machine's IP address (e.g. `ipconfig`).
3. Open in any browser:
   ```
   http://<HOST_IP>:8080/HotelReservation/
   ```
