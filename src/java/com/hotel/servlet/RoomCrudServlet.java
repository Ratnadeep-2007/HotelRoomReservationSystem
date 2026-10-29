package com.hotel.servlet;

import com.hotel.dao.RoomDao;
import com.hotel.exception.InvalidBookingException;
import com.hotel.model.Room;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

// Room CRUD servlet. It works with RoomDao and passes data to JSP pages.
// Demonstrates Request, Session, and Application scopes + Exception Handling (Lab 1, 6, 8).
@WebServlet("/room-crud")
public class RoomCrudServlet extends HttpServlet {

    private final RoomDao roomDao = new RoomDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            if ("edit".equals(action)) {
                String idParam = request.getParameter("id");
                if (idParam == null || idParam.trim().isEmpty()) {
                    throw new InvalidBookingException("Room ID is required to edit.");
                }
                int id = Integer.parseInt(idParam.trim());
                Room room = roomDao.getRoom(id);
                if (room == null) {
                    throw new InvalidBookingException("Room with ID " + id + " does not exist.");
                }
                // Setting attribute in request scope for room-form.jsp
                request.setAttribute("room", room);
                request.getRequestDispatcher("room-form.jsp").forward(request, response);

            } else if ("delete".equals(action)) {
                String idParam = request.getParameter("id");
                if (idParam == null || idParam.trim().isEmpty()) {
                    throw new InvalidBookingException("Room ID is required to delete.");
                }
                int id = Integer.parseInt(idParam.trim());
                roomDao.deleteRoom(id);
                // Setting attribute in session scope (message shown after redirect)
                request.getSession().setAttribute("message", "Room #" + id + " was successfully deleted.");
                response.sendRedirect("room-crud");

            } else {
                List<Room> rooms = roomDao.getAllRooms();
                // Setting attribute in request scope for rooms.jsp
                request.setAttribute("rooms", rooms);

                // Setting attribute in application scope: count page visits
                Integer visits = (Integer) getServletContext().getAttribute("roomPageVisits");
                if (visits == null) {
                    visits = 0;
                }
                getServletContext().setAttribute("roomPageVisits", visits + 1);

                request.getRequestDispatcher("rooms.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "Invalid numeric ID provided.");
            response.sendRedirect("room-crud");
        } catch (InvalidBookingException e) {
            request.getSession().setAttribute("error", e.getMessage());
            response.sendRedirect("room-crud");
        } catch (SQLException e) {
            request.getSession().setAttribute("error", "Database error: " + e.getMessage());
            response.sendRedirect("room-crud");
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

            // Input Validation with Custom Exception
            if (roomNumber == null || roomNumber.trim().isEmpty()) {
                throw new InvalidBookingException("Room number cannot be empty.");
            }
            if (roomType == null || roomType.trim().isEmpty()) {
                throw new InvalidBookingException("Room type cannot be empty.");
            }

            BigDecimal price;
            try {
                price = new BigDecimal(priceParam.trim());
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new InvalidBookingException("Price per night must be greater than zero.");
                }
            } catch (NumberFormatException | NullPointerException e) {
                throw new InvalidBookingException("Please enter a valid numeric room price.");
            }

            int capacity;
            try {
                capacity = Integer.parseInt(capacityParam.trim());
                if (capacity < 1) {
                    throw new InvalidBookingException("Room capacity must be at least 1 person.");
                }
            } catch (NumberFormatException | NullPointerException e) {
                throw new InvalidBookingException("Please enter a valid integer for capacity.");
            }

            Room room = new Room();
            room.setRoomNumber(roomNumber.trim());
            room.setRoomType(roomType.trim());
            room.setPricePerNight(price);
            room.setCapacity(capacity);
            room.setActive(request.getParameter("active") != null);

            String id = request.getParameter("roomId");
            if (id == null || id.trim().isEmpty()) {
                roomDao.addRoom(room);
                session.setAttribute("message", "Room " + room.getRoomNumber() + " added successfully.");
            } else {
                room.setRoomId(Integer.parseInt(id.trim()));
                roomDao.updateRoom(room);
                session.setAttribute("message", "Room " + room.getRoomNumber() + " updated successfully.");
            }
            response.sendRedirect("room-crud");

        } catch (InvalidBookingException e) {
            session.setAttribute("error", e.getMessage());
            response.sendRedirect("room-form.jsp");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry in MySQL
                session.setAttribute("error", "A room with that room number already exists. Please choose a unique room number.");
            } else {
                session.setAttribute("error", "Database error: " + e.getMessage());
            }
            response.sendRedirect("room-form.jsp");
        } catch (Exception e) {
            session.setAttribute("error", "Unexpected error: " + e.getMessage());
            response.sendRedirect("room-form.jsp");
        }
    }
}
