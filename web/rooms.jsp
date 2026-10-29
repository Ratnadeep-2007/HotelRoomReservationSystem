<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="hotel.Room"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Room Management (CRUD)</title>
    <style>
        body { font-family: Arial, Helvetica, sans-serif; background-color: #f4f7f6; margin: 20px; }
        h2 { color: #2c3e50; background-color: #ecf0f1; padding: 10px; border-left: 5px solid #27ae60; margin-top: 0; }
        .nav { background-color: #34495e; padding: 10px 15px; margin-bottom: 20px; }
        .nav a { color: #ffffff; text-decoration: none; font-weight: bold; margin-right: 20px; font-size: 14px; }
        .nav a:hover { color: #f39c12; }
        table { border-collapse: collapse; width: 100%; background-color: white; margin-bottom: 25px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        th { background-color: #2980b9; color: white; padding: 10px; border: 1px solid #ddd; text-align: left; }
        td { padding: 9px; border: 1px solid #ddd; }
        tr:nth-child(even) { background-color: #f9f9f9; }
        tr:hover { background-color: #f1f8ff; }
        .box { background-color: #ffffff; border: 1px solid #dcdcdc; border-top: 3px solid #27ae60; padding: 15px; width: 480px; }
        .box h3 { margin-top: 0; color: #27ae60; border-bottom: 1px solid #eee; padding-bottom: 5px; }
        .btn-green { background-color: #27ae60; color: white; border: none; padding: 7px 16px; cursor: pointer; font-weight: bold; }
        .btn-edit { background-color: #f39c12; color: white; padding: 3px 8px; text-decoration: none; font-size: 13px; font-weight: bold; border-radius: 2px; }
        .btn-del { background-color: #e74c3c; color: white; padding: 3px 8px; text-decoration: none; font-size: 13px; font-weight: bold; border-radius: 2px; }
        .msg { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; padding: 8px 12px; margin-bottom: 15px; font-weight: bold; }
        .err { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; padding: 8px 12px; margin-bottom: 15px; font-weight: bold; }
        .status-yes { color: #27ae60; font-weight: bold; }
        .status-no { color: #e74c3c; font-weight: bold; }
    </style>
</head>
<body>
    <h2>Room Management (CRUD - Lab 4)</h2>

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
            <td><b><%= r.getRoomNumber() %></b></td>
            <td><%= r.getRoomType() %></td>
            <td>Rs. <%= r.getPricePerNight() %></td>
            <td><%= r.getCapacity() %> Guest(s)</td>
            <td>
                <% if (r.isActive()) { %>
                    <span class="status-yes">Active</span>
                <% } else { %>
                    <span class="status-no">Inactive</span>
                <% } %>
            </td>
            <td>
                <a href="room-crud?action=edit&id=<%= r.getRoomId() %>" class="btn-edit">Edit</a>
                &nbsp;
                <a href="room-crud?action=delete&id=<%= r.getRoomId() %>" class="btn-del"
                   onclick="return confirm('Delete room <%= r.getRoomNumber() %>?')">Delete</a>
            </td>
        </tr>
        <%
                }
            } else {
        %>
        <tr>
            <td colspan="6" align="center" style="color:#7f8c8d;">No rooms available. Use form below to add one.</td>
        </tr>
        <% } %>
    </table>

    <%-- 2. Add / Edit Room Form --%>
    <%
        Room editRoom = (Room) request.getAttribute("room");
        boolean isEdit = (editRoom != null);
    %>
    <div class="box">
        <h3><%= isEdit ? "Edit Room #" + editRoom.getRoomNumber() : "Add New Room" %></h3>
        <form action="room-crud" method="post">
            <% if (isEdit) { %>
                <input type="hidden" name="roomId" value="<%= editRoom.getRoomId() %>">
            <% } %>

            <p><b>Room Number:</b><br>
            <input type="text" name="roomNumber" required style="width:90%; padding:5px;" value="<%= isEdit ? editRoom.getRoomNumber() : "" %>"></p>

            <p><b>Room Type:</b><br>
            <input type="text" name="roomType" required placeholder="Single, Double, Deluxe, Suite" style="width:90%; padding:5px;" value="<%= isEdit ? editRoom.getRoomType() : "" %>"></p>

            <p><b>Price Per Night (Rs.):</b><br>
            <input type="number" step="0.01" name="pricePerNight" required style="width:90%; padding:5px;" value="<%= isEdit ? editRoom.getPricePerNight() : "" %>"></p>

            <p><b>Capacity (Persons):</b><br>
            <input type="number" name="capacity" required style="width:90%; padding:5px;" value="<%= isEdit ? editRoom.getCapacity() : "" %>"></p>

            <p><label><input type="checkbox" name="active" <%= (!isEdit || editRoom.isActive()) ? "checked" : "" %>> <b>Active</b> (Available for booking)</label></p>

            <input type="submit" class="btn-green" value="<%= isEdit ? "Update Room" : "Save Room" %>">
            <% if (isEdit) { %>
                &nbsp;&nbsp;<a href="room-crud" style="color:#7f8c8d;">Cancel</a>
            <% } %>
        </form>
    </div>

    <p style="color:#7f8c8d; font-size:12px; margin-top:20px;">
        Page Visits (Application Scope): <b><%= application.getAttribute("roomVisits") %></b>
    </p>
</body>
</html>
