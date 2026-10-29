package com.hotel.servlet;

import com.hotel.config.DBConnect;
import com.hotel.exception.InvalidBookingException;
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

        HttpSession session = request.getSession();
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                throw new InvalidBookingException("Reservation ID is required for cancellation.");
            }

            int id;
            try {
                id = Integer.parseInt(idParam.trim());
            } catch (NumberFormatException e) {
                throw new InvalidBookingException("Invalid reservation ID format.");
            }

            String sql = "UPDATE reservations SET booking_status='Cancelled' WHERE reservation_id=? AND booking_status='Booked'";
            try (Connection con = DBConnect.dbCon();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, id);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    session.setAttribute("message", "Reservation #" + id + " has been successfully cancelled.");
                } else {
                    session.setAttribute("error", "Reservation #" + id + " was not found or was already cancelled.");
                }
            }
        } catch (InvalidBookingException e) {
            session.setAttribute("error", e.getMessage());
        } catch (SQLException e) {
            session.setAttribute("error", "Database error during cancellation: " + e.getMessage());
        } catch (Exception e) {
            session.setAttribute("error", "Unexpected error: " + e.getMessage());
        }

        response.sendRedirect("reservations");
    }
}
