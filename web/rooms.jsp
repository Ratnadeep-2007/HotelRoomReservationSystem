<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.hotel.model.Room"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Rooms</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f8fafc; padding: 30px; }
        table { border-collapse: collapse; width: 100%; max-width: 900px; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
        th, td { padding: 12px 16px; text-align: left; border-bottom: 1px solid #e2e8f0; }
        th { background: #f1f5f9; color: #334155; font-weight: 600; }
        tr:hover { background: #f8fafc; }
        .btn { display: inline-block; background: #2563eb; color: white; padding: 8px 16px; border-radius: 6px; text-decoration: none; font-weight: 500; }
        .btn-sm { padding: 4px 10px; font-size: 13px; text-decoration: none; border-radius: 4px; }
        .btn-edit { background: #0284c7; color: white; }
        .btn-del { background: #dc2626; color: white; }
        .alert-success { background: #dcfce7; color: #166534; padding: 10px 14px; border-radius: 6px; margin-bottom: 16px; font-weight: 500; }
        .alert-danger { background: #fee2e2; color: #991b1b; padding: 10px 14px; border-radius: 6px; margin-bottom: 16px; font-weight: 500; }
    </style>
</head>
<body>
    <h2>Room Management (CRUD)</h2>

    <%-- Session Flash Messages --%>
    <%
        String msg = (String) session.getAttribute("message");
        if (msg != null) {
            out.println("<div class='alert-success'>&#10003; " + msg + "</div>");
            session.removeAttribute("message");
        }
        String err = (String) session.getAttribute("error");
        if (err != null) {
            out.println("<div class='alert-danger'>&#9888; " + err + "</div>");
            session.removeAttribute("error");
        }
    %>

    <%-- Request Attribute --%>
    <%
        List<Room> rooms = (List<Room>) request.getAttribute("rooms");
    %>

    <table>
        <tr>
            <th>Room No</th>
            <th>Type</th>
            <th>Price / Night</th>
            <th>Capacity</th>
            <th>Active</th>
            <th>Actions</th>
        </tr>
        <%
            if (rooms != null) {
                for (Room room : rooms) {
        %>
        <tr>
            <td><strong><%= room.getRoomNumber() %></strong></td>
            <td><%= room.getRoomType() %></td>
            <td>Rs. <%= room.getPricePerNight() %></td>
            <td><%= room.getCapacity() %> Guest(s)</td>
            <td><%= room.isActive() ? "Yes" : "No" %></td>
            <td>
                <a href="room-crud?action=edit&id=<%= room.getRoomId() %>" class="btn-sm btn-edit">Edit</a>
                <a href="room-crud?action=delete&id=<%= room.getRoomId() %>" class="btn-sm btn-del"
                   onclick="return confirm('Delete this room?')">Delete</a>
            </td>
        </tr>
        <%
                }
            }
        %>
    </table>

    <p style="margin-top: 20px;">
        <a href="room-form.jsp" class="btn">+ Add New Room</a>
        &nbsp;&nbsp;
        <a href="index.jsp">Back to Home</a>
    </p>

    <%-- Application Scope Counter --%>
    <p style="color: #64748b; font-size: 13px;">Page Visits: <%= application.getAttribute("roomPageVisits") %></p>
</body>
</html>
