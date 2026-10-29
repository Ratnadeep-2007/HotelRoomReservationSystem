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

// Master Controller Servlet handling all Hotel operations (Labs 5, 6, 8)
@WebServlet({"/rooms", "/room-crud", "/reserve", "/reservations", "/cancel", "/guest"})
public class HotelServlet extends HttpServlet {

    private final HotelDao dao = new HotelDao();

    // Demonstrating Servlet Lifecycle (Lab 5)
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("[HotelServlet] init() called: Servlet initialized.");
    }

    @Override
    public void destroy() {
        System.out.println("[HotelServlet] destroy() called: Servlet shut down.");
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        try {
            if ("/rooms".equals(path)) {
                // 1. Search Available Rooms by Date -> Forwards to index.jsp
                String inParam = request.getParameter("checkIn");
                String outParam = request.getParameter("checkOut");

                if (inParam == null || outParam == null || inParam.trim().isEmpty() || outParam.trim().isEmpty()) {
                    response.sendRedirect("index.jsp");
                    return;
                }

                LocalDate checkIn = LocalDate.parse(inParam.trim());
                LocalDate checkOut = LocalDate.parse(outParam.trim());

                if (checkIn.isBefore(LocalDate.now())) {
                    throw new HotelDao.InvalidBookingException("Check-in date cannot be in the past.");
                }
                if (!checkOut.isAfter(checkIn)) {
                    throw new HotelDao.InvalidBookingException("Check-out date must be after check-in date.");
                }

                List<Room> available = dao.getAvailableRooms(checkIn, checkOut);
                request.setAttribute("availableRooms", available);
                request.setAttribute("checkIn", checkIn);
                request.setAttribute("checkOut", checkOut);
                request.getRequestDispatcher("index.jsp").forward(request, response);

            } else if ("/reservations".equals(path)) {
                // 2. View All Reservations -> Forwards to reservations.jsp
                List<HotelDao.ReservationRecord> list = dao.getAllReservations();
                request.setAttribute("reservations", list);
                request.getRequestDispatcher("reservations.jsp").forward(request, response);

            } else {
                // 3. Admin Room Management -> Forwards to rooms.jsp
                String action = request.getParameter("action");
                if ("edit".equals(action)) {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Room room = dao.getRoom(id);
                    request.setAttribute("room", room);
                } else if ("delete".equals(action)) {
                    int id = Integer.parseInt(request.getParameter("id"));
                    dao.deleteRoom(id);
                    request.getSession().setAttribute("message", "Room #" + id + " was deleted.");
                    response.sendRedirect("room-crud");
                    return;
                }

                List<Room> allRooms = dao.getAllRooms();
                request.setAttribute("rooms", allRooms);

                // Application Scope Counter (Lab 6)
                Integer visits = (Integer) getServletContext().getAttribute("roomVisits");
                getServletContext().setAttribute("roomVisits", (visits == null ? 1 : visits + 1));

                request.getRequestDispatcher("rooms.jsp").forward(request, response);
            }
        } catch (DateTimeParseException | HotelDao.InvalidBookingException e) {
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
        String path = request.getServletPath();
        HttpSession session = request.getSession();

        try {
            if ("/guest".equals(path)) {
                // 1. Session Management with JavaBean (Lab 8)
                Person.Guest guest = new Person.Guest();
                guest.setName(request.getParameter("name").trim());
                guest.setPhone(request.getParameter("phone").trim());
                guest.setRoomType(request.getParameter("roomType"));

                session.setAttribute("guest", guest);
                session.setAttribute("message", "Guest profile saved to session!");
                response.sendRedirect("index.jsp");

            } else if ("/cancel".equals(path)) {
                // 2. Cancel existing reservation
                int id = Integer.parseInt(request.getParameter("id"));
                boolean success = dao.cancelReservation(id);
                if (success) {
                    session.setAttribute("message", "Reservation #" + id + " has been cancelled.");
                } else {
                    session.setAttribute("error", "Reservation #" + id + " was not found or already cancelled.");
                }
                response.sendRedirect("reservations");

            } else if ("/reserve".equals(path)) {
                // 3. Create Room Reservation
                String guestName = request.getParameter("guestName");
                String phone = request.getParameter("phone");
                String roomIdParam = request.getParameter("roomId");
                String inParam = request.getParameter("checkIn");
                String outParam = request.getParameter("checkOut");

                if (guestName == null || guestName.trim().isEmpty()) {
                    throw new HotelDao.InvalidBookingException("Guest name is required.");
                }
                if (phone == null || phone.trim().length() < 7) {
                    throw new HotelDao.InvalidBookingException("Valid phone number required.");
                }

                int roomId = Integer.parseInt(roomIdParam.trim());
                LocalDate checkIn = LocalDate.parse(inParam.trim());
                LocalDate checkOut = LocalDate.parse(outParam.trim());

                dao.bookRoom(guestName.trim(), phone.trim(), roomId, checkIn, checkOut);
                session.setAttribute("message", "Room booked successfully!");
                response.sendRedirect("reservations");

            } else {
                // 4. Save/Update Room (/room-crud)
                String roomNumber = request.getParameter("roomNumber");
                String roomType = request.getParameter("roomType");
                String priceParam = request.getParameter("pricePerNight");
                String capacityParam = request.getParameter("capacity");

                if (roomNumber == null || roomNumber.trim().isEmpty()) {
                    throw new HotelDao.InvalidBookingException("Room number cannot be empty.");
                }
                if (roomType == null || roomType.trim().isEmpty()) {
                    throw new HotelDao.InvalidBookingException("Room type cannot be empty.");
                }

                BigDecimal price = new BigDecimal(priceParam.trim());
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new HotelDao.InvalidBookingException("Price must be greater than zero.");
                }

                int capacity = Integer.parseInt(capacityParam.trim());
                if (capacity < 1) {
                    throw new HotelDao.InvalidBookingException("Capacity must be at least 1.");
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
            }
        } catch (HotelDao.InvalidBookingException | HotelDao.RoomUnavailableException | DateTimeParseException | NumberFormatException e) {
            session.setAttribute("error", e.getMessage());
            response.sendRedirect(path.equals("/room-crud") ? "room-crud" : "index.jsp");
        } catch (SQLException e) {
            session.setAttribute("error", (e.getErrorCode() == 1062 ? "Room number already exists." : "Database error: " + e.getMessage()));
            response.sendRedirect(path.equals("/room-crud") ? "room-crud" : "index.jsp");
        }
    }
}
