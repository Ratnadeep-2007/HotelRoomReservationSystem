<%-- 
    Document   : first.jsp
    Created on : 16-Oct-2026, 9:29:33 am
    Author     : ratna
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        first file
        <%
            String x="hi world";
            session.setAttribute("y", x);
            response.sendRedirect("second.jsp");
            %>
    </body>
</html>
