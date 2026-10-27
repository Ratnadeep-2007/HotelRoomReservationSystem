package com.hotel.servlet;

import com.hotel.dao.RoomDao;
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
// Passing data: request attribute "rooms" / "room" -> forwarded to the JSP.
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
            if (action.equals("edit")) {
                int id = Integer.parseInt(request.getParameter("id"));
                Room room = roomDao.getRoom(id);
                // Setting attribute in request scope for room-form.jsp
                request.setAttribute("room", room);
                request.getRequestDispatcher("room-form.jsp").forward(request, response);
            } else if (action.equals("delete")) {
                int id = Integer.parseInt(request.getParameter("id"));
                roomDao.deleteRoom(id);
                // Setting attribute in session scope (message shown after redirect)
                request.getSession().setAttribute("message", "Room deleted.");
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
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Room room = new Room();
        room.setRoomNumber(request.getParameter("roomNumber").trim());
        room.setRoomType(request.getParameter("roomType").trim());
        room.setPricePerNight(new BigDecimal(request.getParameter("pricePerNight").trim()));
        room.setCapacity(Integer.parseInt(request.getParameter("capacity").trim()));
        room.setActive(request.getParameter("active") != null);

        HttpSession session = request.getSession();
        try {
            String id = request.getParameter("roomId");
            if (id == null || id.isEmpty()) {
                roomDao.addRoom(room);
                session.setAttribute("message", "Room added.");
            } else {
                room.setRoomId(Integer.parseInt(id));
                roomDao.updateRoom(room);
                session.setAttribute("message", "Room updated.");
            }
            response.sendRedirect("room-crud");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
