package edu.java.service.exception;

public class ChatAlreadyRegisteredException extends RuntimeException {
    public ChatAlreadyRegisteredException() {
    }

    public ChatAlreadyRegisteredException(String message) {
        super(message);
    }

    public ChatAlreadyRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }
}
