<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.hotel.model.Room"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Rooms</title>
</head>
<body>
    <h2>Rooms</h2>

    <%-- Message comes from the session (set by RoomCrudServlet). --%>
    <%
        String msg = (String) session.getAttribute("message");
        if (msg != null) {
            out.println("<p>" + msg + "</p>");
            session.removeAttribute("message");
        }
    %>

    <%-- The room list comes from the request attribute set by the servlet. --%>
    <%
        List<Room> rooms = (List<Room>) request.getAttribute("rooms");
    %>

    <table border="1" cellpadding="8">
        <tr>
            <th>Room No</th>
            <th>Type</th>
            <th>Price</th>
            <th>Capacity</th>
            <th>Active</th>
            <th>Edit</th>
            <th>Delete</th>
        </tr>
        <%
            if (rooms != null) {
                // Lambda expression with List: print only active rooms count is not
                // needed here, the loop below prints every room row by row.
                for (Room room : rooms) {
        %>
        <tr>
            <td><%= room.getRoomNumber() %></td>
            <td><%= room.getRoomType() %></td>
            <td>Rs. <%= room.getPricePerNight() %></td>
            <td><%= room.getCapacity() %></td>
            <td><%= room.isActive() ? "Yes" : "No" %></td>
            <td><a href="room-crud?action=edit&id=<%= room.getRoomId() %>">Edit</a></td>
            <td><a href="room-crud?action=delete&id=<%= room.getRoomId() %>"
                   onclick="return confirm('Delete this room?')">Delete</a></td>
        </tr>
        <%
                }
            }
        %>
    </table>

    <br>
    <a href="room-form.jsp">Add New Room</a>
    <br><br>
    <a href="index.jsp">Home</a>

    <%-- Application scope attribute set by RoomCrudServlet. --%>
    <p>Room page visits: <%= application.getAttribute("roomPageVisits") %></p>
</body>
</html>
