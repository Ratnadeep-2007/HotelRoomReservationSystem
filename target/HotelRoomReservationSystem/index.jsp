<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.hotel.model.Guest"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hotel Reservation</title>
</head>
<body>
    <h2>Hotel Room Reservation</h2>

    <%-- If a Guest object is in the session, show its name. --%>
    <%
        Guest guest = (Guest) session.getAttribute("guest");
        if (guest != null) {
            out.println("<p>Welcome, " + guest.getName() + "</p>");
        }
    %>

    <h3>Search Rooms</h3>
    <form action="rooms" method="get">
        Check-in: <input type="date" name="checkIn" required>
        <br><br>
        Check-out: <input type="date" name="checkOut" required>
        <br><br>
        <input type="submit" value="Search Rooms">
    </form>

    <h3>Save Guest in Session</h3>
    <form action="guest" method="post">
        Name: <input type="text" name="name" required>
        <br><br>
        Phone: <input type="text" name="phone" required>
        <br><br>
        Room Type:
        <select name="roomType">
            <option>Single</option>
            <option>Double</option>
            <option>Deluxe</option>
            <option>Suite</option>
        </select>
        <br><br>
        <input type="submit" value="Save Guest">
    </form>

    <br>
    <a href="room-crud">Manage Rooms (CRUD)</a>
    <br><br>
    <a href="reservations">View Reservations</a>
    <br><br>
    <a href="guest.jsp">View Session Guest</a>
</body>
</html>
