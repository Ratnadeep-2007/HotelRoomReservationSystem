<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hotel.Guest"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Session Guest Profile</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f8fafc; padding: 30px; margin: 0; }
        .card { max-width: 500px; margin: 0 auto; background: white; padding: 25px; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
    </style>
</head>
<body>
<div class="card">
    <h2>Guest Stored in Session (Lab 8)</h2>

    <%
        Guest guest = (Guest) session.getAttribute("guest");
        if (guest == null) {
            out.println("<p style='color:#64748b;'>No guest currently stored in session scope. First save a guest from the home page.</p>");
        } else {
    %>
        <p><strong>Name:</strong> <%= guest.getName() %></p>
        <p><strong>Phone:</strong> <%= guest.getPhone() %></p>
        <p><strong>Preferred Room:</strong> <%= guest.getRoomType() %></p>
    <%
        }
    %>

    <p style="margin-top:20px;"><a href="index.jsp">&larr; Back to Home</a></p>
</div>
</body>
</html>
