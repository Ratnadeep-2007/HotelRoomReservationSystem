package com.hotel.servlet;

import com.hotel.model.Guest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// Saves a Guest object in the session, then guest.jsp reads it.
// This shows Session Management with objects and passing data between pages.
@WebServlet("/guest")
public class GuestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Guest guest = new Guest();
        guest.setName(request.getParameter("name").trim());
        guest.setPhone(request.getParameter("phone").trim());
        guest.setRoomType(request.getParameter("roomType"));

        // Setting attribute in session scope: the Guest object itself
        HttpSession session = request.getSession();
        session.setAttribute("guest", guest);

        response.sendRedirect("guest.jsp");
    }
}
