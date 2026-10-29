package com.hotel.servlet;

import com.hotel.config.DBConnect;
import com.hotel.exception.InvalidBookingException;
import com.hotel.util.Html;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet("/rooms")
public class RoomListServlet extends HttpServlet {

    // Demonstrating Servlet Lifecycle (Lab 5)
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("[RoomListServlet] init() called: Servlet initialized.");
    }

    @Override
    public void destroy() {
        System.out.println("[RoomListServlet] destroy() called: Servlet shut down.");
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter pw = response.getWriter();

        try {
            String inParam = request.getParameter("checkIn");
            String outParam = request.getParameter("checkOut");

            if (inParam == null || outParam == null || inParam.trim().isEmpty() || outParam.trim().isEmpty()) {
                response.sendRedirect("index.jsp");
                return;
            }

            LocalDate checkIn;
            LocalDate checkOut;
            try {
                checkIn = LocalDate.parse(inParam.trim());
                checkOut = LocalDate.parse(outParam.trim());
            } catch (DateTimeParseException e) {
                throw new InvalidBookingException("Please enter valid calendar dates (YYYY-MM-DD).");
            }

            LocalDate today = LocalDate.now();
            if (checkIn.isBefore(today)) {
                throw new InvalidBookingException("Check-in date cannot be in the past (" + checkIn + ").");
            }
            if (!checkOut.isAfter(checkIn)) {
                throw new InvalidBookingException("Check-out date must be after check-in date.");
            }

            pw.println("<!DOCTYPE html><html><head><title>Available Rooms</title>");
            pw.println("<style>");
            pw.println("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f8fafc; padding: 30px; margin: 0; }");
            pw.println(".container { max-width: 800px; margin: 0 auto; }");
            pw.println(".card { background: white; padding: 20px; border-radius: 10px; margin-bottom: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }");
            pw.println("input[type=text], input[type=tel] { padding: 8px 12px; border: 1px solid #cbd5e1; border-radius: 6px; margin: 4px 0 12px; width: 240px; }");
            pw.println(".btn { background: #2563eb; color: white; border: none; padding: 8px 16px; border-radius: 6px; cursor: pointer; font-weight: 500; }");
            pw.println(".btn:hover { background: #1d4ed8; }");
            pw.println("</style></head><body><div class='container'>");
            pw.println("<h2>Available Rooms (" + checkIn + " to " + checkOut + ")</h2>");

            String sql = "SELECT * FROM rooms WHERE active=TRUE AND room_id NOT IN "
                    + "(SELECT room_id FROM reservations WHERE booking_status='Booked' "
                    + "AND check_in < ? AND check_out > ?) ORDER BY room_number";

            try (Connection con = DBConnect.dbCon();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setDate(1, Date.valueOf(checkOut));
                ps.setDate(2, Date.valueOf(checkIn));

                try (ResultSet rs = ps.executeQuery()) {
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        pw.println("<div class='card'>");
                        pw.println("<h3>Room " + Html.escape(rs.getString("room_number")) + " &mdash; " + Html.escape(rs.getString("room_type")) + "</h3>");
                        pw.println("<p>Capacity: <strong>" + rs.getInt("capacity") + " Guest(s)</strong> | Price: <strong>Rs. " + rs.getBigDecimal("price_per_night") + " / night</strong></p>");
                        pw.println("<form action='reserve' method='post'>");
                        pw.println("<input type='hidden' name='roomId' value='" + rs.getInt("room_id") + "'>");
                        pw.println("<input type='hidden' name='checkIn' value='" + checkIn + "'>");
                        pw.println("<input type='hidden' name='checkOut' value='" + checkOut + "'>");
                        pw.println("<label>Guest Name:<br><input type='text' name='guestName' maxlength='100' placeholder='Full Name' required></label><br>");
                        pw.println("<label>Contact Phone:<br><input type='tel' name='phone' maxlength='25' placeholder='Phone Number' required></label><br>");
                        pw.println("<input type='submit' class='btn' value='Confirm Reservation'>");
                        pw.println("</form></div>");
                    }
                    if (!found) {
                        pw.println("<div class='card'><p>No rooms available for the selected dates. <a href='index.jsp'>Try different dates</a>.</p></div>");
                    }
                }
            } catch (SQLException e) {
                getServletContext().log("Could not load rooms", e);
                pw.println("<div class='card' style='border-left: 4px solid red;'><p>Database connection error. Please verify MySQL service and credentials.</p></div>");
            }

            pw.println("<p><a href='index.jsp'>&larr; Back to Home</a></p>");
            pw.println("</div></body></html>");

        } catch (InvalidBookingException e) {
            pw.println("<!DOCTYPE html><html><body style='font-family:sans-serif; padding:40px;'>");
            pw.println("<h3 style='color:#dc2626;'>Booking Validation Error</h3>");
            pw.println("<p>" + e.getMessage() + "</p>");
            pw.println("<a href='index.jsp'>Back to Search</a>");
            pw.println("</body></html>");
        }
    }
}
