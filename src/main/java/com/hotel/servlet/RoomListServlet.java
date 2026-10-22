package com.hotel.servlet;

import com.hotel.config.DBConnect;
import com.hotel.util.Html;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;

@WebServlet("/rooms")
public class RoomListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter pw = response.getWriter();

        if (request.getParameter("checkIn") == null || request.getParameter("checkOut") == null) {
            response.sendRedirect("index.html");
            return;
        }

        LocalDate checkIn = LocalDate.parse(request.getParameter("checkIn"));
        LocalDate checkOut = LocalDate.parse(request.getParameter("checkOut"));

        if (checkIn.isBefore(LocalDate.now()) || !checkOut.isAfter(checkIn)) {
            pw.println("Choose valid dates. <a href='index.html'>Home</a>");
            return;
        }

        pw.println("<html><head><title>Rooms</title></head><body>");
        pw.println("<h2>Available Rooms</h2>");
        pw.println("<p>" + checkIn + " to " + checkOut + "</p>");

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
                    pw.println("<h3>Room " + Html.escape(rs.getString("room_number")) + "</h3>");
                    pw.println("<p>Type: " + Html.escape(rs.getString("room_type")) + "</p>");
                    pw.println("<p>Capacity: " + rs.getInt("capacity") + "</p>");
                    pw.println("<p>Price per night: Rs. " + rs.getBigDecimal("price_per_night") + "</p>");
                    pw.println("<form action='reserve' method='post'>");
                    pw.println("<input type='hidden' name='roomId' value='" + rs.getInt("room_id") + "'>");
                    pw.println("<input type='hidden' name='checkIn' value='" + checkIn + "'>");
                    pw.println("<input type='hidden' name='checkOut' value='" + checkOut + "'>");
                    pw.println("Guest Name: <input name='guestName' maxlength='100' required><br><br>");
                    pw.println("Phone: <input name='phone' maxlength='25' required><br><br>");
                    pw.println("<input type='submit' value='Book Room'>");
                    pw.println("</form><hr>");
                }
                if (!found) {
                    pw.println("<p>No rooms available.</p>");
                }
            }
        } catch (SQLException e) {
            getServletContext().log("Could not load rooms", e);
            pw.println("<p>Cannot load rooms. Check your MySQL password and run database/hotel_db.sql.</p>");
        }

        pw.println("<a href='index.html'>Home</a>");
        pw.println("</body></html>");
    }
}
