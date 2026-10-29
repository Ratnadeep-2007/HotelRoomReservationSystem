package com.hotel.exception;

/**
 * Thrown when booking parameters (dates, guest details, format) are invalid (Lab 1).
 */
public class InvalidBookingException extends HotelException {

    public InvalidBookingException(String message) {
        super(message);
    }
}
