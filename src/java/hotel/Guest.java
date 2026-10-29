package hotel;

import java.io.Serializable;

// OOP Concept: Guest inherits from Person (Lab 4)
// Stored as a session attribute object (Lab 8)
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
