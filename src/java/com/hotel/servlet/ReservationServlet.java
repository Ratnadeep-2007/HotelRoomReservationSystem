package com.hotel.servlet;

import com.hotel.config.DBConnect;
import com.hotel.exception.InvalidBookingException;
import com.hotel.exception.RoomUnavailableException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

@WebServlet("/reserve")
public class ReservationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            // 1. Validate & Parse Inputs (throws InvalidBookingException on error)
            String guest = request.getParameter("guestName");
            String phone = request.getParameter("phone");
            String roomIdParam = request.getParameter("roomId");
            String checkInParam = request.getParameter("checkIn");
            String checkOutParam = request.getParameter("checkOut");

            if (guest == null || guest.trim().isEmpty()) {
                throw new InvalidBookingException("Guest name cannot be empty.");
            }
            if (phone == null || phone.trim().length() < 7) {
                throw new InvalidBookingException("Please enter a valid contact phone number (at least 7 digits).");
            }
            if (roomIdParam == null || roomIdParam.trim().isEmpty()) {
                throw new InvalidBookingException("No room was selected for reservation.");
            }

            int roomId;
            try {
                roomId = Integer.parseInt(roomIdParam.trim());
            } catch (NumberFormatException e) {
                throw new InvalidBookingException("Invalid Room ID format.");
            }

            LocalDate checkIn;
            LocalDate checkOut;
            try {
                checkIn = LocalDate.parse(checkInParam.trim());
                checkOut = LocalDate.parse(checkOutParam.trim());
            } catch (DateTimeParseException | NullPointerException e) {
                throw new InvalidBookingException("Invalid date format. Please select valid dates.");
            }

            LocalDate today = LocalDate.now();
            if (checkIn.isBefore(today)) {
                throw new InvalidBookingException("Check-in date cannot be in the past (" + checkIn + ").");
            }
            if (!checkOut.isAfter(checkIn)) {
                throw new InvalidBookingException("Check-out date (" + checkOut + ") must be after check-in date (" + checkIn + ").");
            }

            // 2. Perform Transactional Booking in Database (throws RoomUnavailableException)
            processBooking(guest.trim(), phone.trim(), roomId, checkIn, checkOut);

            // Redirect on success
            response.sendRedirect("reservations");

        } catch (InvalidBookingException e) {
            renderErrorPage(response, "Validation Error", e.getMessage(), "index.jsp", "Back to Home");
        } catch (RoomUnavailableException e) {
            renderErrorPage(response, "Room Unavailable", e.getMessage(), "index.jsp", "Search Different Dates/Rooms");
        } catch (SQLException e) {
            renderErrorPage(response, "Database Error", "An error occurred while communicating with the database: " + e.getMessage(), "index.jsp", "Return to Home");
        } catch (Exception e) {
            renderErrorPage(response, "Unexpected Error", "System encountered an unexpected issue: " + e.getMessage(), "index.jsp", "Return to Home");
        }
    }

    private void processBooking(String guest, String phone, int roomId, LocalDate checkIn, LocalDate checkOut)
            throws SQLException, RoomUnavailableException {

        try (Connection con = DBConnect.dbCon()) {
            con.setAutoCommit(false); // Begin transaction
            BigDecimal price;

            // Check room existence & active status with row lock
            String roomSql = "SELECT price_per_night, active FROM rooms WHERE room_id=? FOR UPDATE";
            try (PreparedStatement ps = con.prepareStatement(roomSql)) {
                ps.setInt(1, roomId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || !rs.getBoolean("active")) {
                        con.rollback();
                        throw new RoomUnavailableException("Selected room (ID " + roomId + ") is either inactive or does not exist.");
                    }
                    price = rs.getBigDecimal("price_per_night");
                }
            }

            // Check if already booked for these dates
            String checkSql = "SELECT reservation_id FROM reservations WHERE room_id=? "
                    + "AND booking_status='Booked' AND check_in < ? AND check_out > ? FOR UPDATE";
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setInt(1, roomId);
                ps.setDate(2, Date.valueOf(checkOut));
                ps.setDate(3, Date.valueOf(checkIn));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        con.rollback();
                        throw new RoomUnavailableException("Room is already reserved by another guest for the selected dates ("
                                + checkIn + " to " + checkOut + ").");
                    }
                }
            }

            // Calculate nights & total
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            BigDecimal total = price.multiply(BigDecimal.valueOf(nights));

            // Insert reservation
            String insertSql = "INSERT INTO reservations "
                    + "(guest_name, phone, room_id, check_in, check_out, total_amount) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setString(1, guest);
                ps.setString(2, phone);
                ps.setInt(3, roomId);
                ps.setDate(4, Date.valueOf(checkIn));
                ps.setDate(5, Date.valueOf(checkOut));
                ps.setBigDecimal(6, total);
                ps.executeUpdate();
            }

            con.commit(); // Commit transaction
        }
    }

    private void renderErrorPage(HttpServletResponse response, String errorType, String errorMessage, String linkHref, String linkText)
            throws IOException {
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Reservation Notice</title>");
        out.println("<style>");
        out.println("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f4f6f9; padding: 40px 20px; }");
        out.println(".card { max-width: 520px; margin: 0 auto; background: white; padding: 30px; border-radius: 12px; box-shadow: 0 4px 16px rgba(0,0,0,0.08); border-top: 5px solid #e53e3e; }");
        out.println("h2 { color: #e53e3e; margin-top: 0; font-size: 22px; }");
        out.println("p { color: #4a5568; line-height: 1.6; font-size: 15px; }");
        out.println(".btn { display: inline-block; background: #3182ce; color: white; padding: 10px 20px; border-radius: 6px; text-decoration: none; font-weight: 500; margin-top: 15px; }");
        out.println(".btn:hover { background: #2b6cb0; }");
        out.println("</style></head><body>");
        out.println("<div class='card'>");
        out.println("<h2>&#9888; " + errorType + "</h2>");
        out.println("<p>" + errorMessage + "</p>");
        out.println("<a href='" + linkHref + "' class='btn'>" + linkText + "</a>");
        out.println("</div></body></html>");
    }
}
