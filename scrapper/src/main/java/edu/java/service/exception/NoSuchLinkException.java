package edu.java.service.exception;

public class NoSuchLinkException extends RuntimeException {
    public NoSuchLinkException() {
    }

    public NoSuchLinkException(String message) {
        super(message);
    }

    public NoSuchLinkException(String message, Throwable cause) {
        super(message, cause);
    }
}
