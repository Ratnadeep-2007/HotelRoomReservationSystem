<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.hotel.model.Guest"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Session Guest</title>
</head>
<body>
    <h2>Guest Stored in Session</h2>

    <%-- This page receives data from another page through the session.
         The Guest object was set by GuestServlet (session.setAttribute). --%>
    <%
        Guest guest = (Guest) session.getAttribute("guest");
        if (guest == null) {
            out.println("<p>No guest in session. First save a guest from the home page.</p>");
        } else {
    %>
        <p>Name: <%= guest.getName() %></p>
        <p>Phone: <%= guest.getPhone() %></p>
        <p>Room Type: <%= guest.getRoomType() %></p>
    <%
        }
    %>

    <br>
    <a href="index.jsp">Home</a>
</body>
</html>
