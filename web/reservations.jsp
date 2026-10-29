<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="hotel.HotelDao.ReservationRecord"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reservation Records</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f8fafc; padding: 30px; margin: 0; }
        .container { max-width: 1000px; margin: 0 auto; }
        table { border-collapse: collapse; width: 100%; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.08); margin-top: 15px; }
        th, td { padding: 12px 14px; text-align: left; border-bottom: 1px solid #e2e8f0; font-size: 14px; }
        th { background: #f1f5f9; color: #334155; font-weight: 600; }
        .status-booked { color: #15803d; font-weight: 600; background: #dcfce7; padding: 3px 8px; border-radius: 4px; display: inline-block; }
        .status-cancelled { color: #b91c1c; font-weight: 600; background: #fee2e2; padding: 3px 8px; border-radius: 4px; display: inline-block; }
        .btn-cancel { background: #ef4444; color: white; border: none; padding: 5px 12px; border-radius: 4px; cursor: pointer; font-size: 13px; }
        .btn-cancel:hover { background: #dc2626; }
        .alert-msg { background: #dcfce7; color: #166534; padding: 10px 14px; border-radius: 6px; margin-bottom: 15px; font-weight: 500; }
        .alert-err { background: #fee2e2; color: #991b1b; padding: 10px 14px; border-radius: 6px; margin-bottom: 15px; font-weight: 500; }
    </style>
</head>
<body>
<div class="container">
    <h2>Reservation Records</h2>

    <%
        String msg = (String) session.getAttribute("message");
        if (msg != null) {
            out.println("<div class='alert-msg'>&#10003; " + msg + "</div>");
            session.removeAttribute("message");
        }
        String err = (String) session.getAttribute("error");
        if (err != null) {
            out.println("<div class='alert-err'>&#9888; " + err + "</div>");
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
            <td>#<%= r.getReservationId() %></td>
            <td><strong><%= r.getGuestName() %></strong></td>
            <td><%= r.getPhone() %></td>
            <td>Room <%= r.getRoomNumber() %></td>
            <td><%= r.getCheckIn() %></td>
            <td><%= r.getCheckOut() %></td>
            <td><strong>Rs. <%= r.getTotalAmount() %></strong></td>
            <% if ("Booked".equalsIgnoreCase(r.getBookingStatus())) { %>
                <td><span class="status-booked">Booked</span></td>
                <td>
                    <form action="cancel" method="post" onsubmit="return confirm('Cancel this reservation?');" style="margin:0;">
                        <input type="hidden" name="id" value="<%= r.getReservationId() %>">
                        <input type="submit" class="btn-cancel" value="Cancel">
                    </form>
                </td>
            <% } else { %>
                <td><span class="status-cancelled">Cancelled</span></td>
                <td style="color:#94a3b8;">-</td>
            <% } %>
        </tr>
        <%
                }
            } else {
        %>
        <tr>
            <td colspan="9" style="text-align:center; padding:20px; color:#64748b;">No reservations found yet.</td>
        </tr>
        <%
            }
        %>
    </table>

    <p style="margin-top:20px;"><a href="index.jsp">&larr; Back to Home</a></p>
</div>
</body>
</html>
