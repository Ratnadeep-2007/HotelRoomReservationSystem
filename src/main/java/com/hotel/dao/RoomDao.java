package com.hotel.dao;

import com.hotel.config.DBConnect;
import com.hotel.model.Room;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// CRUD operations on the rooms table using JDBC.
// Every select reads a ResultSet and stores the rows in a Java List.
public class RoomDao {

    // CREATE
    public void addRoom(Room room) throws SQLException {
        String sql = "INSERT INTO rooms (room_number, room_type, price_per_night, capacity, active) "
                + "VALUES (?, ?, ?, ?, ?)";
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

    // READ all rooms
    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        try (Connection con = DBConnect.dbCon();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rooms.add(readRoom(rs));
            }
        }
        // Lambda expression: sort the list by price, low to high.
        rooms.sort((a, b) -> a.getPricePerNight().compareTo(b.getPricePerNight()));
        return rooms;
    }

    // READ one room by id
    public Room getRoom(int roomId) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try (Connection con = DBConnect.dbCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return readRoom(rs);
                }
            }
        }
        return null;
    }

    // UPDATE
    public void updateRoom(Room room) throws SQLException {
        String sql = "UPDATE rooms SET room_number = ?, room_type = ?, "
                + "price_per_night = ?, capacity = ?, active = ? WHERE room_id = ?";
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

    // Helper: convert one ResultSet row into a Room object.
    private Room readRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setRoomType(rs.getString("room_type"));
        room.setPricePerNight(rs.getBigDecimal("price_per_night"));
        room.setCapacity(rs.getInt("capacity"));
        room.setActive(rs.getBoolean("active"));
        return room;
    }
}
