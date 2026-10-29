package hotel;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Data Access Object handling all JDBC operations (Labs 2, 3, 4, 6)
public class HotelDao {

    // --- INNER CLASS FOR RESERVATION RECORDS ---
    public static class ReservationRecord {
        private int reservationId;
        private String guestName;
        private String phone;
        private String roomNumber;
        private LocalDate checkIn;
        private LocalDate checkOut;
        private BigDecimal totalAmount;
        private String bookingStatus;

        public int getReservationId() { return reservationId; }
        public void setReservationId(int id) { this.reservationId = id; }

        public String getGuestName() { return guestName; }
        public void setGuestName(String name) { this.guestName = name; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getRoomNumber() { return roomNumber; }
        public void setRoomNumber(String no) { this.roomNumber = no; }

        public LocalDate getCheckIn() { return checkIn; }
        public void setCheckIn(LocalDate in) { this.checkIn = in; }

        public LocalDate getCheckOut() { return checkOut; }
        public void setCheckOut(LocalDate out) { this.checkOut = out; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal amount) { this.totalAmount = amount; }

        public String getBookingStatus() { return bookingStatus; }
        public void setBookingStatus(String status) { this.bookingStatus = status; }
    }

    // --- ROOM CRUD (Lab 4) ---

    // CREATE
    public void addRoom(Room room) throws SQLException {
        String sql = "INSERT INTO rooms (room_number, room_type, price_per_night, capacity, active) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnect.dbCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getRoomType());
            ps.setBigDecimal(3, room.getPricePerNight());
            ps.setInt(4, room.getCapacity());
            ps.setBoolean(5, room.isActive());
            ps.executeUpdate();
        }
    }

    // READ ALL ROOMS (Lab 2: ArrayList, Lab 3: Lambda sort)
    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>(); // Collections (Lab 2)
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        try (Connection con = DBConnect.dbCon();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rooms.add(mapRoom(rs));
            }
        }
        // Lambda Expression: sort rooms by price ascending (Lab 3)
        rooms.sort((r1, r2) -> r1.getPricePerNight().compareTo(r2.getPricePerNight()));
        return rooms;
    }

    // READ AVAILABLE ROOMS FOR DATE RANGE
    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE active = TRUE AND room_id NOT IN "
                + "(SELECT room_id FROM reservations WHERE booking_status = 'Booked' "
                + "AND check_in < ? AND check_out > ?) ORDER BY room_number";
        try (Connection con = DBConnect.dbCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(checkOut));
            ps.setDate(2, Date.valueOf(checkIn));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapRoom(rs));
                }
            }
        }
        return rooms;
    }

    // READ ONE ROOM
    public Room getRoom(int roomId) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try (Connection con = DBConnect.dbCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRoom(rs);
                }
            }
        }
        return null;
    }

    // UPDATE
    public void updateRoom(Room room) throws SQLException {
        String sql = "UPDATE rooms SET room_number = ?, room_type = ?, price_per_night = ?, capacity = ?, active = ? WHERE room_id = ?";
        try (Connection con = DBConnect.dbCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getRoomType());
            ps.setBigDecimal(3, room.getPricePerNight());
            ps.setInt(4, room.getCapacity());
            ps.setBoolean(5, room.isActive());
            ps.setInt(6, room.getRoomId());
            ps.executeUpdate();
        }
    }

    // DELETE
    public void deleteRoom(int roomId) throws SQLException {
        String sql = "DELETE FROM rooms WHERE room_id = ?";
        try (Connection con = DBConnect.dbCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.executeUpdate();
        }
    }

    // --- RESERVATION OPERATIONS ---

    // BOOK ROOM (Transactional JDBC)
    public void bookRoom(String guestName, String phone, int roomId, LocalDate checkIn, LocalDate checkOut)
            throws SQLException, HotelException.RoomUnavailableException {

        try (Connection con = DBConnect.dbCon()) {
            con.setAutoCommit(false); // Begin transaction

            BigDecimal price;
            String checkRoom = "SELECT price_per_night, active FROM rooms WHERE room_id = ? FOR UPDATE";
            try (PreparedStatement ps = con.prepareStatement(checkRoom)) {
                ps.setInt(1, roomId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || !rs.getBoolean("active")) {
                        con.rollback();
                        throw new HotelException.RoomUnavailableException("Selected room is inactive or not found.");
                    }
                    price = rs.getBigDecimal("price_per_night");
                }
            }

            String checkConflict = "SELECT reservation_id FROM reservations WHERE room_id = ? "
                    + "AND booking_status = 'Booked' AND check_in < ? AND check_out > ? FOR UPDATE";
            try (PreparedStatement ps = con.prepareStatement(checkConflict)) {
                ps.setInt(1, roomId);
                ps.setDate(2, Date.valueOf(checkOut));
                ps.setDate(3, Date.valueOf(checkIn));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        con.rollback();
                        throw new HotelException.RoomUnavailableException("Room is already booked for these dates.");
                    }
                }
            }

            long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
            BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(nights));

            String insert = "INSERT INTO reservations (guest_name, phone, room_id, check_in, check_out, total_amount) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(insert)) {
                ps.setString(1, guestName);
                ps.setString(2, phone);
                ps.setInt(3, roomId);
                ps.setDate(4, Date.valueOf(checkIn));
                ps.setDate(5, Date.valueOf(checkOut));
                ps.setBigDecimal(6, totalAmount);
                ps.executeUpdate();
            }

            con.commit();
        }
    }

    // READ ALL RESERVATIONS
    public List<ReservationRecord> getAllReservations() throws SQLException {
        List<ReservationRecord> list = new ArrayList<>();
        String sql = "SELECT b.*, r.room_number FROM reservations b "
                + "JOIN rooms r ON b.room_id = r.room_id ORDER BY b.reservation_id DESC";
        try (Connection con = DBConnect.dbCon();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ReservationRecord rec = new ReservationRecord();
                rec.setReservationId(rs.getInt("reservation_id"));
                rec.setGuestName(rs.getString("guest_name"));
                rec.setPhone(rs.getString("phone"));
                rec.setRoomNumber(rs.getString("room_number"));
                rec.setCheckIn(rs.getDate("check_in").toLocalDate());
                rec.setCheckOut(rs.getDate("check_out").toLocalDate());
                rec.setTotalAmount(rs.getBigDecimal("total_amount"));
                rec.setBookingStatus(rs.getString("booking_status"));
                list.add(rec);
            }
        }
        return list;
    }

    // CANCEL RESERVATION
    public boolean cancelReservation(int reservationId) throws SQLException {
        String sql = "UPDATE reservations SET booking_status = 'Cancelled' WHERE reservation_id = ? AND booking_status = 'Booked'";
        try (Connection con = DBConnect.dbCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            return ps.executeUpdate() > 0;
        }
    }

    private Room mapRoom(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setRoomId(rs.getInt("room_id"));
        r.setRoomNumber(rs.getString("room_number"));
        r.setRoomType(rs.getString("room_type"));
        r.setPricePerNight(rs.getBigDecimal("price_per_night"));
        r.setCapacity(rs.getInt("capacity"));
        r.setActive(rs.getBoolean("active"));
        return r;
    }
}
