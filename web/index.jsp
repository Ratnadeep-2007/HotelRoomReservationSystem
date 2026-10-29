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
        body { font-family: Arial, Helvetica, sans-serif; background-color: #f4f7f6; margin: 20px; }
        h2 { color: #2c3e50; background-color: #ecf0f1; padding: 10px; border-left: 5px solid #3498db; margin-top: 0; }
        .nav { background-color: #34495e; padding: 10px 15px; margin-bottom: 20px; }
        .nav a { color: #ffffff; text-decoration: none; font-weight: bold; margin-right: 20px; font-size: 14px; }
        .nav a:hover { color: #f39c12; }
        .box { background-color: #ffffff; border: 1px solid #dcdcdc; border-top: 3px solid #3498db; padding: 15px; margin-bottom: 20px; }
        .box h3 { margin-top: 0; color: #2980b9; border-bottom: 1px solid #eee; padding-bottom: 5px; }
        .btn-green { background-color: #27ae60; color: white; border: none; padding: 6px 14px; cursor: pointer; font-weight: bold; }
        .btn-blue { background-color: #2980b9; color: white; border: none; padding: 6px 14px; cursor: pointer; font-weight: bold; }
        .btn-green:hover { background-color: #219150; }
        .btn-blue:hover { background-color: #1f618d; }
        .msg { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; padding: 8px 12px; margin-bottom: 15px; font-weight: bold; }
        .err { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; padding: 8px 12px; margin-bottom: 15px; font-weight: bold; }
        .room-card { background-color: #fdfefe; border: 1px solid #bdc3c7; border-left: 4px solid #e67e22; padding: 12px; margin-top: 10px; }
    </style>
</head>
<body>
    <h2>Hotel Room Reservation Portal</h2>

    <div class="nav">
        <a href="index.jsp">Home</a>
        <a href="room-crud">Manage Rooms (CRUD)</a>
        <a href="reservations">View Reservations</a>
    </div>

    <%-- Session Flash Messages --%>
    <%
        String err = (String) session.getAttribute("error");
        if (err != null) {
            out.println("<div class='err'>Error: " + err + "</div>");
            session.removeAttribute("error");
        }
        String msg = (String) session.getAttribute("message");
        if (msg != null) {
            out.println("<div class='msg'>" + msg + "</div>");
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
        <h3>1. Guest Session Profile (Lab 8)</h3>
        <% if (guest != null) { %>
            <p style="background-color: #e8f8f5; border: 1px solid #a3e4d7; padding: 8px; color: #117864;">
                Active Session: <b><%= guest.getName() %></b> (Phone: <%= guest.getPhone() %>, Room: <%= guest.getRoomType() %>)
            </p>
        <% } %>
        <form action="guest" method="post">
            <b>Guest Name:</b> <input type="text" name="name" required value="<%= defaultName %>" placeholder="Your Name">
            &nbsp;&nbsp;
            <b>Phone:</b> <input type="text" name="phone" required value="<%= defaultPhone %>" placeholder="Phone Number">
            &nbsp;&nbsp;
            <b>Room Preference:</b>
            <select name="roomType">
                <option <%= (guest != null && "Single".equals(guest.getRoomType())) ? "selected" : "" %>>Single</option>
                <option <%= (guest != null && "Double".equals(guest.getRoomType())) ? "selected" : "" %>>Double</option>
                <option <%= (guest != null && "Deluxe".equals(guest.getRoomType())) ? "selected" : "" %>>Deluxe</option>
                <option <%= (guest != null && "Suite".equals(guest.getRoomType())) ? "selected" : "" %>>Suite</option>
            </select>
            &nbsp;&nbsp;
            <input type="submit" class="btn-green" value="Save Profile">
        </form>
    </div>

    <%-- 2. Search Available Rooms --%>
    <div class="box">
        <h3>2. Search & Book Rooms</h3>
        <form action="rooms" method="get">
            <b>Check-in:</b> <input type="date" name="checkIn" required value="${checkIn}">
            &nbsp;&nbsp;
            <b>Check-out:</b> <input type="date" name="checkOut" required value="${checkOut}">
            &nbsp;&nbsp;
            <input type="submit" class="btn-blue" value="Check Availability">
        </form>

        <%-- Available Rooms List --%>
        <%
            List<Room> available = (List<Room>) request.getAttribute("availableRooms");
            if (available != null) {
        %>
            <h4 style="color:#2c3e50; margin-top:20px;">Available Rooms (${checkIn} to ${checkOut}):</h4>
            <% if (!available.isEmpty()) {
                for (Room room : available) {
            %>
            <div class="room-card">
                <p style="margin: 0 0 8px 0;">
                    <b style="color:#2980b9; font-size:16px;">Room <%= room.getRoomNumber() %></b> &mdash; <%= room.getRoomType() %> |
                    Capacity: <b><%= room.getCapacity() %> Guest(s)</b> |
                    Price: <b style="color:#c0392b;">Rs. <%= room.getPricePerNight() %></b> / night
                </p>
                <form action="reserve" method="post">
                    <input type="hidden" name="roomId" value="<%= room.getRoomId() %>">
                    <input type="hidden" name="checkIn" value="${checkIn}">
                    <input type="hidden" name="checkOut" value="${checkOut}">
                    <b>Guest Name:</b> <input type="text" name="guestName" required value="<%= defaultName %>">
                    &nbsp;&nbsp;
                    <b>Phone:</b> <input type="text" name="phone" required value="<%= defaultPhone %>">
                    &nbsp;&nbsp;
                    <input type="submit" class="btn-green" value="Book Now">
                </form>
            </div>
            <%
                }
            } else {
            %>
                <p style="color:#7f8c8d;">No rooms available for the selected dates.</p>
            <% } %>
        <% } %>
    </div>
</body>
</html>
