package edu.java.service.exception;

public class LinkAlreadyTrackingException extends RuntimeException {
    public LinkAlreadyTrackingException() {
    }

    public LinkAlreadyTrackingException(String message) {
        super(message);
    }

    public LinkAlreadyTrackingException(String message, Throwable cause) {
        super(message, cause);
    }
}
