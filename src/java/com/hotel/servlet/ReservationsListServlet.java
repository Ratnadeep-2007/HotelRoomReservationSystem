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
        HttpSession session = request.getSession();

        pw.println("<!DOCTYPE html><html><head><title>Reservation Records</title>");
        pw.println("<style>");
        pw.println("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f8fafc; padding: 30px; margin: 0; }");
        pw.println(".container { max-width: 1000px; margin: 0 auto; }");
        pw.println("table { border-collapse: collapse; width: 100%; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.08); margin-top: 15px; }");
        pw.println("th, td { padding: 12px 14px; text-align: left; border-bottom: 1px solid #e2e8f0; font-size: 14px; }");
        pw.println("th { background: #f1f5f9; color: #334155; font-weight: 600; }");
        pw.println(".status-booked { color: #15803d; font-weight: 600; background: #dcfce7; padding: 3px 8px; border-radius: 4px; display: inline-block; }");
        pw.println(".status-cancelled { color: #b91c1c; font-weight: 600; background: #fee2e2; padding: 3px 8px; border-radius: 4px; display: inline-block; }");
        pw.println(".btn-cancel { background: #ef4444; color: white; border: none; padding: 5px 12px; border-radius: 4px; cursor: pointer; font-size: 13px; }");
        pw.println(".btn-cancel:hover { background: #dc2626; }");
        pw.println(".alert-msg { background: #dcfce7; color: #166534; padding: 10px 14px; border-radius: 6px; margin-bottom: 15px; font-weight: 500; }");
        pw.println(".alert-err { background: #fee2e2; color: #991b1b; padding: 10px 14px; border-radius: 6px; margin-bottom: 15px; font-weight: 500; }");
        pw.println("</style></head><body><div class='container'>");
        pw.println("<h2>Hotel Reservation Records</h2>");

        // Display session flash messages
        String msg = (String) session.getAttribute("message");
        if (msg != null) {
            pw.println("<div class='alert-msg'>&#10003; " + Html.escape(msg) + "</div>");
            session.removeAttribute("message");
        }
        String err = (String) session.getAttribute("error");
        if (err != null) {
            pw.println("<div class='alert-err'>&#9888; " + Html.escape(err) + "</div>");
            session.removeAttribute("error");
        }

        pw.println("<table>");
        pw.println("<tr><th>ID</th><th>Guest Name</th><th>Phone</th><th>Room</th>"
                + "<th>Check-in</th><th>Check-out</th><th>Total Amount</th><th>Status</th><th>Action</th></tr>");

        String sql = "SELECT b.*, r.room_number FROM reservations b "
                + "JOIN rooms r ON b.room_id=r.room_id ORDER BY b.reservation_id DESC";

        try (Connection con = DBConnect.dbCon();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                String status = rs.getString("booking_status");
                pw.println("<tr>");
                pw.println("<td>#" + rs.getInt("reservation_id") + "</td>");
                pw.println("<td><strong>" + Html.escape(rs.getString("guest_name")) + "</strong></td>");
                pw.println("<td>" + Html.escape(rs.getString("phone")) + "</td>");
                pw.println("<td>Room " + Html.escape(rs.getString("room_number")) + "</td>");
                pw.println("<td>" + rs.getDate("check_in") + "</td>");
                pw.println("<td>" + rs.getDate("check_out") + "</td>");
                pw.println("<td><strong>Rs. " + rs.getBigDecimal("total_amount") + "</strong></td>");
                
                if ("Booked".equalsIgnoreCase(status)) {
                    pw.println("<td><span class='status-booked'>Booked</span></td>");
                    pw.println("<td>");
                    pw.println("<form action='cancel' method='post' onsubmit=\"return confirm('Are you sure you want to cancel this reservation?');\" style='margin:0;'>");
                    pw.println("<input type='hidden' name='id' value='" + rs.getInt("reservation_id") + "'>");
                    pw.println("<input type='submit' class='btn-cancel' value='Cancel'>");
                    pw.println("</form></td>");
                } else {
                    pw.println("<td><span class='status-cancelled'>Cancelled</span></td>");
                    pw.println("<td style='color:#94a3b8;'>-</td>");
                }
                pw.println("</tr>");
            }

            if (!hasRows) {
                pw.println("<tr><td colspan='9' style='text-align:center; padding:20px; color:#64748b;'>No reservations found yet.</td></tr>");
            }

        } catch (SQLException e) {
            pw.println("<tr><td colspan='9' style='color:#dc2626;'>Database query failed: " + e.getMessage() + "</td></tr>");
        }

        pw.println("</table>");
        pw.println("<p style='margin-top:20px;'><a href='index.jsp'>&larr; Back to Home</a></p>");
        pw.println("</div></body></html>");
    }
}
