<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="hotel.Room"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Room Management (CRUD)</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f8fafc; padding: 30px 15px; margin: 0; color: #1e293b; }
        .container { max-width: 900px; margin: 0 auto; }
        .card { background: white; padding: 24px; border-radius: 10px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); margin-bottom: 24px; }
        h2, h3 { margin-top: 0; color: #0f172a; }
        table { border-collapse: collapse; width: 100%; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.08); margin-bottom: 25px; }
        th, td { padding: 12px 14px; text-align: left; border-bottom: 1px solid #e2e8f0; font-size: 14px; }
        th { background: #f1f5f9; color: #334155; font-weight: 600; }
        tr:hover { background: #f8fafc; }
        .btn { background: #2563eb; color: white; border: none; padding: 9px 18px; border-radius: 6px; cursor: pointer; font-weight: 500; font-size: 14px; }
        .btn:hover { background: #1d4ed8; }
        .btn-sm { padding: 5px 10px; font-size: 13px; text-decoration: none; border-radius: 4px; }
        .btn-edit { background: #0284c7; color: white; }
        .btn-del { background: #dc2626; color: white; }
        .alert-success { background: #dcfce7; color: #166534; padding: 10px 14px; border-radius: 6px; margin-bottom: 16px; font-weight: 500; }
        .alert-danger { background: #fee2e2; color: #991b1b; padding: 10px 14px; border-radius: 6px; margin-bottom: 16px; font-weight: 500; }
        input[type=text], input[type=number] { width: 100%; box-sizing: border-box; padding: 8px 12px; border: 1px solid #cbd5e1; border-radius: 6px; margin-top: 4px; }
        label { display: block; margin-bottom: 12px; font-weight: 500; color: #475569; font-size: 14px; }
    </style>
</head>
<body>
<div class="container">
    <h2>Room Management (CRUD - Lab 4)</h2>

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

    <%-- 1. Room Inventory Table --%>
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
            if (rooms != null && !rooms.isEmpty()) {
                for (Room r : rooms) {
        %>
        <tr>
            <td><strong><%= r.getRoomNumber() %></strong></td>
            <td><%= r.getRoomType() %></td>
            <td>Rs. <%= r.getPricePerNight() %></td>
            <td><%= r.getCapacity() %> Guest(s)</td>
            <td><%= r.isActive() ? "Yes" : "No" %></td>
            <td>
                <a href="room-crud?action=edit&id=<%= r.getRoomId() %>" class="btn-sm btn-edit">Edit</a>
                <a href="room-crud?action=delete&id=<%= r.getRoomId() %>" class="btn-sm btn-del"
                   onclick="return confirm('Delete room <%= r.getRoomNumber() %>?')">Delete</a>
            </td>
        </tr>
        <%
                }
            } else {
        %>
        <tr>
            <td colspan="6" style="text-align:center; padding:20px; color:#64748b;">No rooms in database. Use form below to add one.</td>
        </tr>
        <% } %>
    </table>

    <%-- 2. Add / Edit Room Form --%>
    <%
        Room editRoom = (Room) request.getAttribute("room");
        boolean isEdit = (editRoom != null);
    %>
    <div class="card">
        <h3><%= isEdit ? "Edit Room #" + editRoom.getRoomNumber() : "Add New Room" %></h3>
        <form action="room-crud" method="post">
            <% if (isEdit) { %>
                <input type="hidden" name="roomId" value="<%= editRoom.getRoomId() %>">
            <% } %>

            <div style="display:grid; grid-template-columns: 1fr 1fr; gap:15px;">
                <label>Room Number:
                    <input type="text" name="roomNumber" required placeholder="e.g. 201"
                           value="<%= isEdit ? editRoom.getRoomNumber() : "" %>">
                </label>
                <label>Room Type:
                    <input type="text" name="roomType" required placeholder="e.g. Deluxe, Suite"
                           value="<%= isEdit ? editRoom.getRoomType() : "" %>">
                </label>
                <label>Price Per Night (Rs.):
                    <input type="number" step="0.01" name="pricePerNight" required placeholder="e.g. 3500.00"
                           value="<%= isEdit ? editRoom.getPricePerNight() : "" %>">
                </label>
                <label>Capacity (Persons):
                    <input type="number" name="capacity" required placeholder="e.g. 2"
                           value="<%= isEdit ? editRoom.getCapacity() : "" %>">
                </label>
            </div>

            <label style="display:flex; align-items:center; gap:8px; margin: 10px 0 16px;">
                <input type="checkbox" name="active" <%= (!isEdit || editRoom.isActive()) ? "checked" : "" %> style="width:auto; margin:0;">
                Active (Available for guest booking)
            </label>

            <input type="submit" class="btn" value="<%= isEdit ? "Update Room" : "Add Room" %>">
            <% if (isEdit) { %>
                &nbsp;&nbsp;<a href="room-crud" style="color:#64748b; text-decoration:none;">Cancel Edit</a>
            <% } %>
        </form>
    </div>

    <p>
        <a href="index.jsp">&larr; Back to Home</a>
        &nbsp;&bull;&nbsp;
        <span style="color:#64748b; font-size:13px;">Total Page Visits (Application Scope): <%= application.getAttribute("roomVisits") %></span>
    </p>
</div>
</body>
</html>
