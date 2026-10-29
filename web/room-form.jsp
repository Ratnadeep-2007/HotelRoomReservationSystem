<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hotel.Room"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Room Form</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f8fafc; padding: 30px; margin: 0; }
        .container { max-width: 500px; margin: 0 auto; background: white; padding: 25px 30px; border-radius: 10px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
        label { display: block; margin-bottom: 15px; font-weight: 500; color: #334155; }
        input[type=text], input[type=number] { width: 100%; box-sizing: border-box; padding: 8px 12px; border: 1px solid #cbd5e1; border-radius: 6px; margin-top: 4px; }
        .btn { background: #2563eb; color: white; border: none; padding: 10px 18px; border-radius: 6px; cursor: pointer; font-weight: 500; font-size: 14px; }
        .btn:hover { background: #1d4ed8; }
        .alert-error { background: #fee2e2; color: #991b1b; padding: 10px 14px; border-radius: 6px; margin-bottom: 15px; font-weight: 500; }
    </style>
</head>
<body>
<div class="container">
    <%
        Room room = (Room) request.getAttribute("room");
        boolean edit = (room != null);
    %>

    <h2><%= edit ? "Edit Room" : "Add Room" %></h2>

    <%
        String err = (String) session.getAttribute("error");
        if (err != null) {
            out.println("<div class='alert-error'>&#9888; " + err + "</div>");
            session.removeAttribute("error");
        }
    %>

    <form action="room-crud" method="post">
        <% if (edit) { %>
            <input type="hidden" name="roomId" value="<%= room.getRoomId() %>">
        <% } %>

        <label>Room Number:
            <input type="text" name="roomNumber" required value="<%= edit ? room.getRoomNumber() : "" %>">
        </label>

        <label>Room Type:
            <input type="text" name="roomType" required placeholder="e.g. Deluxe, Suite, Single" value="<%= edit ? room.getRoomType() : "" %>">
        </label>

        <label>Price Per Night (Rs.):
            <input type="number" step="0.01" name="pricePerNight" required value="<%= edit ? room.getPricePerNight() : "" %>">
        </label>

        <label>Capacity (Persons):
            <input type="number" name="capacity" required value="<%= edit ? room.getCapacity() : "" %>">
        </label>

        <label style="display:flex; align-items:center; gap:8px;">
            <input type="checkbox" name="active" <%= (!edit || room.isActive()) ? "checked" : "" %>>
            Active (Available for booking)
        </label>

        <input type="submit" class="btn" value="<%= edit ? "Update Room" : "Save Room" %>">
        &nbsp;&nbsp;
        <a href="room-crud">Cancel</a>
    </form>
</div>
</body>
</html>
