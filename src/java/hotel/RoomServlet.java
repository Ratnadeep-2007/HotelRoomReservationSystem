package hotel;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

// Controller for Room Search & Admin CRUD (Labs 5, 6)
@WebServlet({"/rooms", "/room-crud"})
public class RoomServlet extends HttpServlet {

    private final HotelDao dao = new HotelDao();

    // Demonstrating Servlet Lifecycle (Lab 5)
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("[RoomServlet] init() called");
    }

    @Override
    public void destroy() {
        System.out.println("[RoomServlet] destroy() called");
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        try {
            if ("/rooms".equals(path)) {
                // Room Search by Dates
                String inParam = request.getParameter("checkIn");
                String outParam = request.getParameter("checkOut");

                if (inParam == null || outParam == null || inParam.trim().isEmpty() || outParam.trim().isEmpty()) {
                    response.sendRedirect("index.jsp");
                    return;
                }

                LocalDate checkIn = LocalDate.parse(inParam.trim());
                LocalDate checkOut = LocalDate.parse(outParam.trim());

                if (checkIn.isBefore(LocalDate.now())) {
                    throw new HotelException.InvalidBookingException("Check-in date cannot be in the past.");
                }
                if (!checkOut.isAfter(checkIn)) {
                    throw new HotelException.InvalidBookingException("Check-out date must be after check-in date.");
                }

                List<Room> available = dao.getAvailableRooms(checkIn, checkOut);
                request.setAttribute("rooms", available);
                request.setAttribute("checkIn", checkIn);
                request.setAttribute("checkOut", checkOut);
                request.getRequestDispatcher("available-rooms.jsp").forward(request, response);

            } else {
                // Admin Room Management (/room-crud)
                String action = request.getParameter("action");
                if ("edit".equals(action)) {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Room room = dao.getRoom(id);
                    request.setAttribute("room", room);
                    request.getRequestDispatcher("room-form.jsp").forward(request, response);

                } else if ("delete".equals(action)) {
                    int id = Integer.parseInt(request.getParameter("id"));
                    dao.deleteRoom(id);
                    request.getSession().setAttribute("message", "Room #" + id + " was deleted.");
                    response.sendRedirect("room-crud");

                } else {
                    List<Room> allRooms = dao.getAllRooms();
                    request.setAttribute("rooms", allRooms);

                    // Application Scope Counter (Lab 6)
                    Integer visits = (Integer) getServletContext().getAttribute("roomVisits");
                    getServletContext().setAttribute("roomVisits", (visits == null ? 1 : visits + 1));

                    request.getRequestDispatcher("rooms.jsp").forward(request, response);
                }
            }
        } catch (DateTimeParseException | HotelException.InvalidBookingException e) {
            request.getSession().setAttribute("error", e.getMessage());
            response.sendRedirect("index.jsp");
        } catch (SQLException e) {
            request.getSession().setAttribute("error", "Database error: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        try {
            String roomNumber = request.getParameter("roomNumber");
            String roomType = request.getParameter("roomType");
            String priceParam = request.getParameter("pricePerNight");
            String capacityParam = request.getParameter("capacity");

            if (roomNumber == null || roomNumber.trim().isEmpty()) {
                throw new HotelException.InvalidBookingException("Room number cannot be empty.");
            }
            if (roomType == null || roomType.trim().isEmpty()) {
                throw new HotelException.InvalidBookingException("Room type cannot be empty.");
            }

            BigDecimal price = new BigDecimal(priceParam.trim());
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new HotelException.InvalidBookingException("Price must be greater than zero.");
            }

            int capacity = Integer.parseInt(capacityParam.trim());
            if (capacity < 1) {
                throw new HotelException.InvalidBookingException("Capacity must be at least 1.");
            }

            Room room = new Room();
            room.setRoomNumber(roomNumber.trim());
            room.setRoomType(roomType.trim());
            room.setPricePerNight(price);
            room.setCapacity(capacity);
            room.setActive(request.getParameter("active") != null);

            String idParam = request.getParameter("roomId");
            if (idParam == null || idParam.trim().isEmpty()) {
                dao.addRoom(room);
                session.setAttribute("message", "Room " + room.getRoomNumber() + " added successfully.");
            } else {
                room.setRoomId(Integer.parseInt(idParam.trim()));
                dao.updateRoom(room);
                session.setAttribute("message", "Room " + room.getRoomNumber() + " updated successfully.");
            }
            response.sendRedirect("room-crud");

        } catch (HotelException.InvalidBookingException | NumberFormatException e) {
            session.setAttribute("error", e.getMessage());
            response.sendRedirect("room-form.jsp");
        } catch (SQLException e) {
            session.setAttribute("error", (e.getErrorCode() == 1062 ? "Room number already exists." : "Database error: " + e.getMessage()));
            response.sendRedirect("room-form.jsp");
        }
    }
}
