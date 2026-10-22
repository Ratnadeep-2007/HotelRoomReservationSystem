<%-- 
    Document   : second.jsp
    Created on : 16-Oct-2026, 9:29:42 am
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
            String a=(String)session.getAttribute("y");
            out.println(a);
            out.println("2nd.jsp");
            
        
            %>
    </body>
</html>
