package hotel;

// Custom Exception Hierarchy for Hotel System (Lab 1)
public class HotelException extends Exception {

    public HotelException(String message) {
        super(message);
    }

    // Custom exception thrown when input parameters (dates, fields) fail validation
    public static class InvalidBookingException extends HotelException {
        public InvalidBookingException(String message) {
            super(message);
        }
    }

    // Custom exception thrown when requested room is unavailable or booked
    public static class RoomUnavailableException extends HotelException {
        public RoomUnavailableException(String message) {
            super(message);
        }
    }
}
