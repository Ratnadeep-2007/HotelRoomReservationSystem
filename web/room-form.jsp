<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.hotel.model.Room"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Room Form</title>
</head>
<body>
    <%-- One form is used for both Add and Edit.
         For Edit, the servlet sets the "room" request attribute. --%>
    <%
        Room room = (Room) request.getAttribute("room");
        boolean edit = (room != null);
    %>

    <h2><%= edit ? "Edit Room" : "Add Room" %></h2>

    <%
        String err = (String) session.getAttribute("error");
        if (err != null) {
            out.println("<p style='color:#dc2626; background:#fee2e2; padding:10px; border-radius:6px; font-weight:500;'>&#9888; " + err + "</p>");
            session.removeAttribute("error");
        }
    %>

    <form action="room-crud" method="post">
        <% if (edit) { %>
            <input type="hidden" name="roomId" value="<%= room.getRoomId() %>">
        <% } %>

        Room Number:
        <input type="text" name="roomNumber" required
               value="<%= edit ? room.getRoomNumber() : "" %>">
        <br><br>

        Room Type:
        <input type="text" name="roomType" required
               value="<%= edit ? room.getRoomType() : "" %>">
        <br><br>

        Price Per Night:
        <input type="number" step="0.01" name="pricePerNight" required
               value="<%= edit ? room.getPricePerNight() : "" %>">
        <br><br>

        Capacity:
        <input type="number" name="capacity" required
               value="<%= edit ? room.getCapacity() : "" %>">
        <br><br>

        Active:
        <input type="checkbox" name="active"
               <%= (edit && room.isActive()) ? "checked" : "" %>>
        <br><br>

        <input type="submit" value="<%= edit ? "Update Room" : "Add Room" %>">
    </form>

    <br>
    <a href="room-crud">Back to Rooms</a>
</body>
</html>
