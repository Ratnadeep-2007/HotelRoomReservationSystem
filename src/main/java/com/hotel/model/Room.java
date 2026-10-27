package com.hotel.model;

import java.io.Serializable;
import java.math.BigDecimal;

// Simple model class for one row of the rooms table.
public class Room implements Serializable {

    private int roomId;
    private String roomNumber;
    private String roomType;
    private BigDecimal pricePerNight;
    private int capacity;
    private boolean active;

    public Room() {
    }

    public Room(int roomId, String roomNumber, String roomType,
                BigDecimal pricePerNight, int capacity, boolean active) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.active = active;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + roomType + ")";
    }
}
