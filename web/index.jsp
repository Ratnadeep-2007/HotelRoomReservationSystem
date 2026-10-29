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
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f8fafc; padding: 30px 15px; margin: 0; color: #1e293b; }
        .container { max-width: 800px; margin: 0 auto; }
        .card { background: white; padding: 24px; border-radius: 10px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); margin-bottom: 24px; }
        h2 { margin-top: 0; color: #0f172a; }
        h3 { margin-top: 0; color: #334155; border-bottom: 2px solid #e2e8f0; padding-bottom: 8px; }
        label { display: block; margin-bottom: 12px; font-weight: 500; color: #475569; }
        input[type=text], input[type=tel], input[type=date], select { width: 100%; box-sizing: border-box; padding: 9px 12px; border: 1px solid #cbd5e1; border-radius: 6px; margin-top: 4px; }
        .btn { background: #2563eb; color: white; border: none; padding: 10px 18px; border-radius: 6px; cursor: pointer; font-weight: 500; font-size: 14px; }
        .btn:hover { background: #1d4ed8; }
        .alert-error { background: #fee2e2; color: #991b1b; padding: 12px 16px; border-radius: 6px; margin-bottom: 20px; font-weight: 500; }
        .alert-success { background: #dcfce7; color: #166534; padding: 12px 16px; border-radius: 6px; margin-bottom: 20px; font-weight: 500; }
        .nav-links { display: flex; gap: 15px; margin-bottom: 24px; }
        .nav-links a { background: white; border: 1px solid #cbd5e1; padding: 8px 14px; border-radius: 6px; text-decoration: none; color: #2563eb; font-weight: 500; }
        .nav-links a:hover { background: #f1f5f9; }
        .room-item { border: 1px solid #e2e8f0; padding: 16px; border-radius: 8px; margin-top: 14px; background: #fafafa; }
    </style>
</head>
<body>
<div class="container">
    <h2>Hotel Room Reservation Portal</h2>

    <div class="nav-links">
        <a href="room-crud">&rarr; Manage Rooms (Admin CRUD)</a>
        <a href="reservations">&rarr; View All Reservations</a>
    </div>

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

    <%-- 1. Session Guest Profile (Lab 8) --%>
    <%
        Guest guest = (Guest) session.getAttribute("guest");
        String defaultName = (guest != null ? guest.getName() : "");
        String defaultPhone = (guest != null ? guest.getPhone() : "");
    %>
    <div class="card">
        <h3>1. Guest Session Profile (Lab 8)</h3>
        <% if (guest != null) { %>
            <p style="background:#e0f2fe; color:#0369a1; padding:10px 14px; border-radius:6px;">
                Active Guest: <strong><%= guest.getName() %></strong> | Phone: <strong><%= guest.getPhone() %></strong> | Preference: <strong><%= guest.getRoomType() %></strong>
            </p>
        <% } %>
        <form action="guest" method="post" style="display:grid; grid-template-columns: 1fr 1fr 1fr auto; gap: 10px; align-items: end;">
            <label style="margin:0;">Name:
                <input type="text" name="name" required placeholder="Full Name" value="<%= defaultName %>">
            </label>
            <label style="margin:0;">Phone:
                <input type="tel" name="phone" required placeholder="Phone Number" value="<%= defaultPhone %>">
            </label>
            <label style="margin:0;">Preference:
                <select name="roomType">
                    <option <%= (guest != null && "Single".equals(guest.getRoomType())) ? "selected" : "" %>>Single</option>
                    <option <%= (guest != null && "Double".equals(guest.getRoomType())) ? "selected" : "" %>>Double</option>
                    <option <%= (guest != null && "Deluxe".equals(guest.getRoomType())) ? "selected" : "" %>>Deluxe</option>
                    <option <%= (guest != null && "Suite".equals(guest.getRoomType())) ? "selected" : "" %>>Suite</option>
                </select>
            </label>
            <input type="submit" class="btn" style="background:#059669;" value="Save Profile">
        </form>
    </div>

    <%-- 2. Search Available Rooms --%>
    <div class="card">
        <h3>2. Search & Book Rooms</h3>
        <form action="rooms" method="get" style="display:grid; grid-template-columns: 1fr 1fr auto; gap: 12px; align-items: end;">
            <label style="margin:0;">Check-in Date:
                <input type="date" name="checkIn" required value="${checkIn}">
            </label>
            <label style="margin:0;">Check-out Date:
                <input type="date" name="checkOut" required value="${checkOut}">
            </label>
            <input type="submit" class="btn" value="Search Available Rooms">
        </form>

        <%-- Available Rooms Search Results List --%>
        <%
            List<Room> available = (List<Room>) request.getAttribute("availableRooms");
            if (available != null) {
        %>
            <h4 style="margin: 20px 0 10px; color:#1e293b;">Available Rooms (${checkIn} to ${checkOut}):</h4>
            <% if (!available.isEmpty()) {
                for (Room room : available) {
            %>
            <div class="room-item">
                <div style="display:flex; justify-content:space-between; align-items:center;">
                    <div>
                        <strong style="font-size:16px;">Room <%= room.getRoomNumber() %></strong> &mdash; <%= room.getRoomType() %>
                        <br>
                        <span style="color:#64748b;">Capacity: <%= room.getCapacity() %> Guest(s) | Rate: Rs. <%= room.getPricePerNight() %>/night</span>
                    </div>
                </div>
                <form action="reserve" method="post" style="margin-top:12px; display:grid; grid-template-columns: 1fr 1fr auto; gap:10px; align-items:end;">
                    <input type="hidden" name="roomId" value="<%= room.getRoomId() %>">
                    <input type="hidden" name="checkIn" value="${checkIn}">
                    <input type="hidden" name="checkOut" value="${checkOut}">
                    <label style="margin:0;">Guest Name:
                        <input type="text" name="guestName" required value="<%= defaultName %>" placeholder="Guest Name">
                    </label>
                    <label style="margin:0;">Phone Number:
                        <input type="tel" name="phone" required value="<%= defaultPhone %>" placeholder="Phone Number">
                    </label>
                    <input type="submit" class="btn" value="Book Room">
                </form>
            </div>
            <%
                }
            } else {
            %>
                <p style="color:#64748b; margin-top:15px;">No rooms available for the selected dates. Please try different dates.</p>
            <% } %>
        <% } %>
    </div>
</div>
</body>
</html>
