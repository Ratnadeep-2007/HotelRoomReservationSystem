package com.hotel.servlet;

import com.hotel.config.DBConnect;
import com.hotel.util.Html;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/reservations")
public class ReservationsListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        pw.println("<html><head><title>Reservations</title></head><body>");
        pw.println("<h2>Reservations</h2>");
        pw.println("<table border='1' cellpadding='8'>");
        pw.println("<tr><th>ID</th><th>Guest</th><th>Phone</th><th>Room</th>"
                + "<th>Check-in</th><th>Check-out</th><th>Total</th><th>Status</th><th>Cancel</th></tr>");

        String sql = "SELECT b.*, r.room_number FROM reservations b "
                + "JOIN rooms r ON b.room_id=r.room_id ORDER BY b.reservation_id DESC";

        try (Connection con = DBConnect.dbCon();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pw.println("<tr>");
                pw.println("<td>" + rs.getInt("reservation_id") + "</td>");
                pw.println("<td>" + Html.escape(rs.getString("guest_name")) + "</td>");
                pw.println("<td>" + Html.escape(rs.getString("phone")) + "</td>");
                pw.println("<td>" + Html.escape(rs.getString("room_number")) + "</td>");
                pw.println("<td>" + rs.getDate("check_in") + "</td>");
                pw.println("<td>" + rs.getDate("check_out") + "</td>");
                pw.println("<td>Rs. " + rs.getBigDecimal("total_amount") + "</td>");
                pw.println("<td>" + Html.escape(rs.getString("booking_status")) + "</td>");
                pw.println("<td>");

                if ("Booked".equals(rs.getString("booking_status"))) {
                    pw.println("<form action='cancel' method='post'>");
                    pw.println("<input type='hidden' name='id' value='" + rs.getInt("reservation_id") + "'>");
                    pw.println("<input type='submit' value='Cancel'>");
                    pw.println("</form>");
                }

                pw.println("</td></tr>");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        pw.println("</table><br>");
        pw.println("<a href='index.jsp'>Home</a>");
        pw.println("</body></html>");
    }
}
