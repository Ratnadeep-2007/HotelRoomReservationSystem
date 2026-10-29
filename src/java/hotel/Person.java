package hotel;

import java.io.Serializable;

// Base class demonstrating OOP / Inheritance (Lab 4)
public class Person implements Serializable {

    private String name;
    private String phone;

    public Person() {
    }

    public Person(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Subclass demonstrating Inheritance (Lab 4) and Session Bean (Lab 8)
    public static class Guest extends Person {
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
}
