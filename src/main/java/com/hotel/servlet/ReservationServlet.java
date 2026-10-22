package com.hotel.servlet;

import com.hotel.config.DBConnect;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@WebServlet("/reserve")
public class ReservationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String guest = request.getParameter("guestName").trim();
        String phone = request.getParameter("phone").trim();
        int roomId = Integer.parseInt(request.getParameter("roomId"));
        LocalDate checkIn = LocalDate.parse(request.getParameter("checkIn"));
        LocalDate checkOut = LocalDate.parse(request.getParameter("checkOut"));

        if (guest.isEmpty() || phone.isEmpty() || checkIn.isBefore(LocalDate.now())
                || !checkOut.isAfter(checkIn)) {
            response.getWriter().println("Enter guest details and valid dates. <a href='index.html'>Home</a>");
            return;
        }

        try (Connection con = DBConnect.dbCon()) {
            // Lock the room until this booking is finished.
            con.setAutoCommit(false);
            BigDecimal price;

            String roomSql = "SELECT price_per_night FROM rooms WHERE room_id=? AND active=TRUE FOR UPDATE";
            try (PreparedStatement ps = con.prepareStatement(roomSql)) {
                ps.setInt(1, roomId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        response.getWriter().println("Room not found. <a href='index.html'>Home</a>");
                        return;
                    }
                    price = rs.getBigDecimal("price_per_night");
                }
            }

            String checkSql = "SELECT reservation_id FROM reservations WHERE room_id=? "
                    + "AND booking_status='Booked' AND check_in < ? AND check_out > ? FOR UPDATE";
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setInt(1, roomId);
                ps.setDate(2, Date.valueOf(checkOut));
                ps.setDate(3, Date.valueOf(checkIn));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        con.rollback();
                        response.getWriter().println("Room already booked. <a href='index.html'>Search again</a>");
                        return;
                    }
                }
            }

            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            BigDecimal total = price.multiply(BigDecimal.valueOf(nights));

            String sql = "INSERT INTO reservations "
                    + "(guest_name,phone,room_id,check_in,check_out,total_amount) VALUES (?,?,?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, guest);
                ps.setString(2, phone);
                ps.setInt(3, roomId);
                ps.setDate(4, Date.valueOf(checkIn));
                ps.setDate(5, Date.valueOf(checkOut));
                ps.setBigDecimal(6, total);
                ps.executeUpdate();
            }

            con.commit();
            response.sendRedirect("reservations");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
