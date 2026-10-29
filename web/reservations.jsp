<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="hotel.HotelDao.ReservationRecord"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reservation Records</title>
    <style>
        body { font-family: Arial, Helvetica, sans-serif; background-color: #f4f7f6; margin: 20px; }
        h2 { color: #2c3e50; background-color: #ecf0f1; padding: 10px; border-left: 5px solid #8e44ad; margin-top: 0; }
        .nav { background-color: #34495e; padding: 10px 15px; margin-bottom: 20px; }
        .nav a { color: #ffffff; text-decoration: none; font-weight: bold; margin-right: 20px; font-size: 14px; }
        .nav a:hover { color: #f39c12; }
        table { border-collapse: collapse; width: 100%; background-color: white; margin-bottom: 25px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        th { background-color: #8e44ad; color: white; padding: 10px; border: 1px solid #ddd; text-align: left; }
        td { padding: 9px; border: 1px solid #ddd; font-size: 14px; }
        tr:nth-child(even) { background-color: #f9f9f9; }
        tr:hover { background-color: #f4ecf7; }
        .status-booked { background-color: #d4edda; color: #155724; padding: 3px 6px; font-weight: bold; border-radius: 3px; }
        .status-cancelled { background-color: #f8d7da; color: #721c24; padding: 3px 6px; font-weight: bold; border-radius: 3px; }
        .btn-cancel { background-color: #e74c3c; color: white; border: none; padding: 4px 10px; cursor: pointer; font-weight: bold; }
        .btn-cancel:hover { background-color: #c0392b; }
        .msg { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; padding: 8px 12px; margin-bottom: 15px; font-weight: bold; }
        .err { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; padding: 8px 12px; margin-bottom: 15px; font-weight: bold; }
    </style>
</head>
<body>
    <h2>Hotel Reservation Records</h2>

    <div class="nav">
        <a href="index.jsp">&larr; Home</a>
        <a href="room-crud">Manage Rooms</a>
        <a href="reservations">View Reservations</a>
    </div>

    <%-- Session Flash Messages --%>
    <%
        String msg = (String) session.getAttribute("message");
        if (msg != null) {
            out.println("<div class='msg'>" + msg + "</div>");
            session.removeAttribute("message");
        }
        String err = (String) session.getAttribute("error");
        if (err != null) {
            out.println("<div class='err'>Error: " + err + "</div>");
            session.removeAttribute("error");
        }
    %>

    <table>
        <tr>
            <th>ID</th>
            <th>Guest Name</th>
            <th>Phone</th>
            <th>Room</th>
            <th>Check-in</th>
            <th>Check-out</th>
            <th>Total Amount</th>
            <th>Booking Status</th>
            <th>Action</th>
        </tr>
        <%
            List<ReservationRecord> list = (List<ReservationRecord>) request.getAttribute("reservations");
            if (list != null && !list.isEmpty()) {
                for (ReservationRecord r : list) {
        %>
        <tr>
            <td>#<%= r.getReservationId() %></td>
            <td><b><%= r.getGuestName() %></b></td>
            <td><%= r.getPhone() %></td>
            <td>Room <%= r.getRoomNumber() %></td>
            <td><%= r.getCheckIn() %></td>
            <td><%= r.getCheckOut() %></td>
            <td><b>Rs. <%= r.getTotalAmount() %></b></td>
            <td>
                <% if ("Booked".equalsIgnoreCase(r.getBookingStatus())) { %>
                    <span class="status-booked">Booked</span>
                <% } else { %>
                    <span class="status-cancelled">Cancelled</span>
                <% } %>
            </td>
            <td>
                <% if ("Booked".equalsIgnoreCase(r.getBookingStatus())) { %>
                    <form action="cancel" method="post" onsubmit="return confirm('Cancel reservation #<%= r.getReservationId() %>?');" style="margin:0;">
                        <input type="hidden" name="id" value="<%= r.getReservationId() %>">
                        <input type="submit" class="btn-cancel" value="Cancel">
                    </form>
                <% } else { %>
                    -
                <% } %>
            </td>
        </tr>
        <%
                }
            } else {
        %>
        <tr>
            <td colspan="9" align="center" style="color:#7f8c8d;">No reservations found in database.</td>
        </tr>
        <%
            }
        %>
    </table>
</body>
</html>
