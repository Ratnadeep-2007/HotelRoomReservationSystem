package com.hotel.model;

import java.io.Serializable;

// OOP concept: Guest inherits name and phone from Person.
// This object is stored in the session (Session Management with objects).
public class Guest extends Person implements Serializable {

    private String roomType;

    public Guest() {
    }

    public Guest(String name, String phone, String roomType) {
        super(name, phone);
        this.roomType = roomType;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
}
