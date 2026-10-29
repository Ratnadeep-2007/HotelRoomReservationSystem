<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="hotel.Room"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Room Management (CRUD)</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { border-collapse: collapse; width: 100%; margin: 15px 0; }
        th, td { border: 1px solid black; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .msg { color: green; font-weight: bold; margin: 10px 0; }
        .err { color: red; font-weight: bold; margin: 10px 0; }
        .box { border: 1px solid #ccc; padding: 15px; margin-top: 20px; width: 450px; }
    </style>
</head>
<body>
    <h2>Room Management (CRUD - Lab 4)</h2>

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
            <th>Action</th>
        </tr>
        <%
            if (rooms != null && !rooms.isEmpty()) {
                for (Room r : rooms) {
        %>
        <tr>
            <td><b><%= r.getRoomNumber() %></b></td>
            <td><%= r.getRoomType() %></td>
            <td>Rs. <%= r.getPricePerNight() %></td>
            <td><%= r.getCapacity() %></td>
            <td><%= r.isActive() ? "Yes" : "No" %></td>
            <td>
                <a href="room-crud?action=edit&id=<%= r.getRoomId() %>">Edit</a> | 
                <a href="room-crud?action=delete&id=<%= r.getRoomId() %>"
                   onclick="return confirm('Delete room <%= r.getRoomNumber() %>?')">Delete</a>
            </td>
        </tr>
        <%
                }
            } else {
        %>
        <tr>
            <td colspan="6" align="center">No rooms available in database.</td>
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

            <p>Room Number:<br>
            <input type="text" name="roomNumber" required value="<%= isEdit ? editRoom.getRoomNumber() : "" %>"></p>

            <p>Room Type:<br>
            <input type="text" name="roomType" required value="<%= isEdit ? editRoom.getRoomType() : "" %>"></p>

            <p>Price Per Night (Rs.):<br>
            <input type="number" step="0.01" name="pricePerNight" required value="<%= isEdit ? editRoom.getPricePerNight() : "" %>"></p>

            <p>Capacity (Persons):<br>
            <input type="number" name="capacity" required value="<%= isEdit ? editRoom.getCapacity() : "" %>"></p>

            <p><label><input type="checkbox" name="active" <%= (!isEdit || editRoom.isActive()) ? "checked" : "" %>> Active</label></p>

            <input type="submit" value="<%= isEdit ? "Update Room" : "Add Room" %>">
            <% if (isEdit) { %>
                &nbsp;&nbsp;<a href="room-crud">Cancel</a>
            <% } %>
        </form>
    </div>

    <p style="color:gray; font-size:12px; margin-top:20px;">
        Page Visits (Application Scope): <%= application.getAttribute("roomVisits") %>
    </p>
</body>
</html>
