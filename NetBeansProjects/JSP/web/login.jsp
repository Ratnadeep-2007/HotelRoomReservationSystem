<%-- 
    Document   : login
    Created on : 19-Oct-2026, 3:19:50 pm
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
        <form action="" method="post">
            
            User Id: <Input type="text" name="t1"><br>
            Password: <input type="text"name=t2"><br>
            
            <input type ="Submit" value="Login">  
            
        </form>
        
        <%
            String msg=(String)session.getAttribute("message");
             if(msg!=null){
             out.println(msg);
             }
          %>   
             
        
        
    </body>
</html>
