<%-- 
    Document   : main.jsp
    Created on : 19-Oct-2026, 3:17:23 pm
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
       
        
        <%
            String x=(String)session.getAttribute("user");
            out.println("Welcom"+x);
            %>
        
        
    </body>
</html>
