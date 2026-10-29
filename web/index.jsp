<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hotel.Person.Guest"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hotel Room Reservation System</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f8fafc; padding: 30px; margin: 0; }
        .container { max-width: 650px; margin: 0 auto; background: white; padding: 25px 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); }
        h2 { color: #1e293b; margin-top: 0; }
        h3 { color: #334155; border-bottom: 2px solid #e2e8f0; padding-bottom: 8px; margin-top: 25px; }
        label { display: block; margin-bottom: 12px; font-weight: 500; color: #475569; }
        input[type=text], input[type=date], select { width: 100%; box-sizing: border-box; padding: 9px 12px; border: 1px solid #cbd5e1; border-radius: 6px; margin-top: 4px; }
        .btn { background: #2563eb; color: white; border: none; padding: 10px 18px; border-radius: 6px; cursor: pointer; font-weight: 500; font-size: 14px; }
        .btn:hover { background: #1d4ed8; }
        .alert-error { background: #fee2e2; color: #991b1b; padding: 10px 14px; border-radius: 6px; margin-bottom: 15px; font-weight: 500; }
        .alert-success { background: #dcfce7; color: #166534; padding: 10px 14px; border-radius: 6px; margin-bottom: 15px; font-weight: 500; }
        .links a { display: inline-block; margin-right: 15px; color: #2563eb; text-decoration: none; font-weight: 500; }
        .links a:hover { text-decoration: underline; }
    </style>
</head>
<body>
<div class="container">
    <h2>Hotel Room Reservation</h2>

    <%-- Session Flash Messages --%>
    <%
        String err = (String) session.getAttribute("error");
        if (err != null) {
            out.println("<div class='alert-error'>&#9888; " + err + "</div>");
            session.removeAttribute("error");
        }
        String msg = (String) session.getAttribute("message");
        if (msg != null) {
            out.println("<div class='alert-success'>&#10003; " + msg + "</div>");
            session.removeAttribute("message");
        }
    %>

    <%-- Session Object Display (Lab 8) --%>
    <%
        Guest guest = (Guest) session.getAttribute("guest");
        if (guest != null) {
            out.println("<p style='background:#f1f5f9; padding:8px 12px; border-radius:6px;'>Welcome, <strong>" + guest.getName() + "</strong> (" + guest.getRoomType() + " preference)</p>");
        }
    %>

    <h3>1. Search Available Rooms</h3>
    <form action="rooms" method="get">
        <label>Check-in Date:
            <input type="date" name="checkIn" required>
        </label>
        <label>Check-out Date:
            <input type="date" name="checkOut" required>
        </label>
        <input type="submit" class="btn" value="Search Available Rooms">
    </form>

    <h3>2. Guest Profile (Session Management - Lab 8)</h3>
    <form action="guest" method="post">
        <label>Guest Full Name:
            <input type="text" name="name" required placeholder="e.g. Shaunak Rane">
        </label>
        <label>Phone Number:
            <input type="text" name="phone" required placeholder="e.g. 9876543210">
        </label>
        <label>Room Preference:
            <select name="roomType">
                <option>Single</option>
                <option>Double</option>
                <option>Deluxe</option>
                <option>Suite</option>
            </select>
        </label>
        <input type="submit" class="btn" style="background:#059669;" value="Save Profile to Session">
    </form>

    <h3>Quick Navigation</h3>
    <div class="links">
        <a href="room-crud">&rarr; Manage Rooms (CRUD)</a>
        <a href="reservations">&rarr; View Reservations</a>
        <a href="guest.jsp">&rarr; View Session Profile</a>
    </div>
</div>
</body>
</html>
