<%-- 
    Document   : Arithmetic
    Created on : 14-Oct-2026, 2:01:41 pm
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
        <h1>Hello World!</h1>
        <form action=""artithmetic.jsp" method="GET">
            Enter first Number:<input type="text" name="t1"><br>
            enter 2nd num:<input type="text" name="t2"> <br>
            <input type="submit" balue="Add">
            <%
                int a =Integer.parseInt(request.getParameter("t1");
                int b =Integer.parseInt(request.getParameter("t2");
                if(a!=0 && b!=0) {
                    int a=Integer.parseInt(x)
                    int b=Integer.parseInt(y)
                    int c=a+b;
                    
             %>
             
             <h3> Sum=<%= c %></h3>
             <%
             
                 
        </form>
    </body>
</html>
