<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="hotel.Room"%>
<%@page import="hotel.Person.Guest"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Available Rooms</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f8fafc; padding: 30px; margin: 0; }
        .container { max-width: 800px; margin: 0 auto; }
        .card { background: white; padding: 20px; border-radius: 8px; margin-bottom: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
        .btn { background: #2563eb; color: white; border: none; padding: 8px 16px; border-radius: 6px; cursor: pointer; font-weight: 500; }
        .btn:hover { background: #1d4ed8; }
        input[type=text], input[type=tel] { padding: 8px 12px; border: 1px solid #cbd5e1; border-radius: 6px; margin: 4px 0 12px; width: 260px; box-sizing: border-box; }
    </style>
</head>
<body>
<div class="container">
    <h2>Available Rooms (${checkIn} to ${checkOut})</h2>

    <%
        List<Room> rooms = (List<Room>) request.getAttribute("rooms");
        Guest sessionGuest = (Guest) session.getAttribute("guest");
        String defaultName = (sessionGuest != null ? sessionGuest.getName() : "");
        String defaultPhone = (sessionGuest != null ? sessionGuest.getPhone() : "");

        if (rooms != null && !rooms.isEmpty()) {
            for (Room room : rooms) {
    %>
    <div class="card">
        <h3 style="margin-top:0;">Room <%= room.getRoomNumber() %> &mdash; <%= room.getRoomType() %></h3>
        <p>Capacity: <strong><%= room.getCapacity() %> Guest(s)</strong> | Price: <strong>Rs. <%= room.getPricePerNight() %> / night</strong></p>
        <form action="reserve" method="post">
            <input type="hidden" name="roomId" value="<%= room.getRoomId() %>">
            <input type="hidden" name="checkIn" value="${checkIn}">
            <input type="hidden" name="checkOut" value="${checkOut}">
            <label>Guest Name:<br>
                <input type="text" name="guestName" value="<%= defaultName %>" required placeholder="Full Name">
            </label><br>
            <label>Phone Number:<br>
                <input type="tel" name="phone" value="<%= defaultPhone %>" required placeholder="Phone Number">
            </label><br>
            <input type="submit" class="btn" value="Confirm Booking">
        </form>
    </div>
    <%
            }
        } else {
    %>
    <div class="card">
        <p>No rooms available for the selected dates. <a href="index.jsp">Try different dates</a>.</p>
    </div>
    <%
        }
    %>

    <p><a href="index.jsp">&larr; Back to Home</a></p>
</div>
</body>
</html>

