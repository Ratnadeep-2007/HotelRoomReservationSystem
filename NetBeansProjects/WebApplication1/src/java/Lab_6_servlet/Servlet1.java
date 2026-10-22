package Lab_6_servlet;

import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Servlet1 extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String str = request.getParameter("txtid");
        //Create Attributes
        request.setAttribute("a", str);
        //created object of RequestDispatcher
        RequestDispatcher rd = request.getRequestDispatcher("Servlet3");
        rd.forward(request, response);
        
        
    }
}