package com.hotel.servlet;

import com.hotel.config.DBConnect;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/cancel")
public class CancelReservationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        String sql = "UPDATE reservations SET booking_status='Cancelled' WHERE reservation_id=?";

        try (Connection con = DBConnect.dbCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            response.sendRedirect("reservations");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
