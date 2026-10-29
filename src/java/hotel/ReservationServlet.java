package hotel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

// Controller for Bookings, Cancellations, and Session Guest (Labs 5, 6, 8)
@WebServlet({"/reserve", "/reservations", "/cancel", "/guest"})
public class ReservationServlet extends HttpServlet {

    private final HotelDao dao = new HotelDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // View All Reservations (/reservations)
        try {
            List<HotelDao.ReservationRecord> list = dao.getAllReservations();
            request.setAttribute("reservations", list);
            request.getRequestDispatcher("reservations.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Database error loading reservations: " + e.getMessage());
            request.getRequestDispatcher("reservations.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        HttpSession session = request.getSession();

        try {
            if ("/guest".equals(path)) {
                // Save Guest object in Session Scope (Lab 8)
                Guest guest = new Guest();
                guest.setName(request.getParameter("name").trim());
                guest.setPhone(request.getParameter("phone").trim());
                guest.setRoomType(request.getParameter("roomType"));

                session.setAttribute("guest", guest);
                response.sendRedirect("guest.jsp");

            } else if ("/cancel".equals(path)) {
                // Cancel existing reservation
                int id = Integer.parseInt(request.getParameter("id"));
                boolean success = dao.cancelReservation(id);
                if (success) {
                    session.setAttribute("message", "Reservation #" + id + " has been cancelled.");
                } else {
                    session.setAttribute("error", "Reservation #" + id + " was not found or already cancelled.");
                }
                response.sendRedirect("reservations");

            } else {
                // Create Room Reservation (/reserve)
                String guestName = request.getParameter("guestName");
                String phone = request.getParameter("phone");
                String roomIdParam = request.getParameter("roomId");
                String inParam = request.getParameter("checkIn");
                String outParam = request.getParameter("checkOut");

                if (guestName == null || guestName.trim().isEmpty()) {
                    throw new HotelException.InvalidBookingException("Guest name is required.");
                }
                if (phone == null || phone.trim().length() < 7) {
                    throw new HotelException.InvalidBookingException("Valid contact phone number required.");
                }

                int roomId = Integer.parseInt(roomIdParam.trim());
                LocalDate checkIn = LocalDate.parse(inParam.trim());
                LocalDate checkOut = LocalDate.parse(outParam.trim());

                dao.bookRoom(guestName.trim(), phone.trim(), roomId, checkIn, checkOut);
                session.setAttribute("message", "Room reserved successfully!");
                response.sendRedirect("reservations");
            }
        } catch (HotelException.InvalidBookingException | HotelException.RoomUnavailableException | DateTimeParseException | NumberFormatException e) {
            session.setAttribute("error", e.getMessage());
            response.sendRedirect("index.jsp");
        } catch (SQLException e) {
            session.setAttribute("error", "Database error: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }
}
