<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="hotel.Room"%>
<%@page import="hotel.Person.Guest"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hotel Room Reservation System</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .msg { color: green; font-weight: bold; margin: 10px 0; }
        .err { color: red; font-weight: bold; margin: 10px 0; }
        .box { border: 1px solid #ccc; padding: 15px; margin-bottom: 20px; }
    </style>
</head>
<body>
    <h2>Hotel Room Reservation System</h2>

    <p>
        <a href="room-crud">Manage Rooms (CRUD)</a> | 
        <a href="reservations">View Reservations</a>
    </p>
    <hr>

    <%-- Session Flash Messages --%>
    <%
        String err = (String) session.getAttribute("error");
        if (err != null) {
            out.println("<p class='err'>Error: " + err + "</p>");
            session.removeAttribute("error");
        }
        String msg = (String) session.getAttribute("message");
        if (msg != null) {
            out.println("<p class='msg'>" + msg + "</p>");
            session.removeAttribute("message");
        }
    %>

    <%-- 1. Session Guest Profile (Lab 8) --%>
    <%
        Guest guest = (Guest) session.getAttribute("guest");
        String defaultName = (guest != null ? guest.getName() : "");
        String defaultPhone = (guest != null ? guest.getPhone() : "");
    %>
    <div class="box">
        <h3>Guest Profile (HttpSession - Lab 8)</h3>
        <% if (guest != null) { %>
            <p>Current Session Guest: <b><%= guest.getName() %></b> | Phone: <b><%= guest.getPhone() %></b> | Room Type: <b><%= guest.getRoomType() %></b></p>
        <% } %>
        <form action="guest" method="post">
            Name: <input type="text" name="name" required value="<%= defaultName %>">
            &nbsp;&nbsp;
            Phone: <input type="text" name="phone" required value="<%= defaultPhone %>">
            &nbsp;&nbsp;
            Preference:
            <select name="roomType">
                <option <%= (guest != null && "Single".equals(guest.getRoomType())) ? "selected" : "" %>>Single</option>
                <option <%= (guest != null && "Double".equals(guest.getRoomType())) ? "selected" : "" %>>Double</option>
                <option <%= (guest != null && "Deluxe".equals(guest.getRoomType())) ? "selected" : "" %>>Deluxe</option>
                <option <%= (guest != null && "Suite".equals(guest.getRoomType())) ? "selected" : "" %>>Suite</option>
            </select>
            &nbsp;&nbsp;
            <input type="submit" value="Save to Session">
        </form>
    </div>

    <%-- 2. Search Available Rooms --%>
    <div class="box">
        <h3>Search & Book Rooms</h3>
        <form action="rooms" method="get">
            Check-in Date: <input type="date" name="checkIn" required value="${checkIn}">
            &nbsp;&nbsp;
            Check-out Date: <input type="date" name="checkOut" required value="${checkOut}">
            &nbsp;&nbsp;
            <input type="submit" value="Search Rooms">
        </form>

        <%-- Available Rooms List --%>
        <%
            List<Room> available = (List<Room>) request.getAttribute("availableRooms");
            if (available != null) {
        %>
            <h4>Available Rooms (${checkIn} to ${checkOut}):</h4>
            <% if (!available.isEmpty()) {
                for (Room room : available) {
            %>
            <div style="border: 1px dashed #999; padding: 10px; margin-top: 10px;">
                <p><b>Room <%= room.getRoomNumber() %></b> (<%= room.getRoomType() %>) &mdash; Capacity: <%= room.getCapacity() %> Person(s) &mdash; Price: Rs. <%= room.getPricePerNight() %>/night</p>
                <form action="reserve" method="post">
                    <input type="hidden" name="roomId" value="<%= room.getRoomId() %>">
                    <input type="hidden" name="checkIn" value="${checkIn}">
                    <input type="hidden" name="checkOut" value="${checkOut}">
                    Guest Name: <input type="text" name="guestName" required value="<%= defaultName %>">
                    &nbsp;&nbsp;
                    Phone: <input type="text" name="phone" required value="<%= defaultPhone %>">
                    &nbsp;&nbsp;
                    <input type="submit" value="Book Room">
                </form>
            </div>
            <%
                }
            } else {
            %>
                <p>No rooms available for these dates.</p>
            <% } %>
        <% } %>
    </div>
</body>
</html>
