<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="hotel.HotelDao.ReservationRecord"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reservation Records</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { border-collapse: collapse; width: 100%; margin: 15px 0; }
        th, td { border: 1px solid black; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .msg { color: green; font-weight: bold; margin: 10px 0; }
        .err { color: red; font-weight: bold; margin: 10px 0; }
    </style>
</head>
<body>
    <h2>Hotel Reservation Records</h2>

    <p><a href="index.jsp">&larr; Back to Home</a></p>
    <hr>

    <%-- Session Flash Messages --%>
    <%
        String msg = (String) session.getAttribute("message");
        if (msg != null) {
            out.println("<p class='msg'>" + msg + "</p>");
            session.removeAttribute("message");
        }
        String err = (String) session.getAttribute("error");
        if (err != null) {
            out.println("<p class='err'>Error: " + err + "</p>");
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
            <th>Status</th>
            <th>Action</th>
        </tr>
        <%
            List<ReservationRecord> list = (List<ReservationRecord>) request.getAttribute("reservations");
            if (list != null && !list.isEmpty()) {
                for (ReservationRecord r : list) {
        %>
        <tr>
            <td><%= r.getReservationId() %></td>
            <td><%= r.getGuestName() %></td>
            <td><%= r.getPhone() %></td>
            <td>Room <%= r.getRoomNumber() %></td>
            <td><%= r.getCheckIn() %></td>
            <td><%= r.getCheckOut() %></td>
            <td>Rs. <%= r.getTotalAmount() %></td>
            <td><b><%= r.getBookingStatus() %></b></td>
            <td>
                <% if ("Booked".equalsIgnoreCase(r.getBookingStatus())) { %>
                    <form action="cancel" method="post" onsubmit="return confirm('Cancel this reservation?');" style="margin:0;">
                        <input type="hidden" name="id" value="<%= r.getReservationId() %>">
                        <input type="submit" value="Cancel">
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
            <td colspan="9" align="center">No reservations found.</td>
        </tr>
        <%
            }
        %>
    </table>
</body>
</html>
