package com.hotel.exception;

/**
 * Thrown when a room is not found, inactive, or already reserved for the chosen date range (Lab 1).
 */
public class RoomUnavailableException extends HotelException {

    public RoomUnavailableException(String message) {
        super(message);
    }
}
