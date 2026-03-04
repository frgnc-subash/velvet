package org.velvet.exception;

public class BookingNotAllowedException extends Exception {
    public BookingNotAllowedException(String message) {
        super(message);
    }
}
