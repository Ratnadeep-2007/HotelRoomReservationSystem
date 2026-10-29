package com.hotel.exception;

/**
 * Base custom application exception for Hotel Reservation System (Lab 1).
 */
public class HotelException extends Exception {

    public HotelException(String message) {
        super(message);
    }

    public HotelException(String message, Throwable cause) {
        super(message, cause);
    }
}
