package edu.java.controller;

import edu.java.controller.dto.ApiErrorResponse;
import edu.java.service.exception.ChatAlreadyRegisteredException;
import edu.java.service.exception.InvalidLinkException;
import edu.java.service.exception.LinkAlreadyTrackingException;
import edu.java.service.exception.NoSuchChatException;
import edu.java.service.exception.NoSuchLinkException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class RestExceptionHandler {
    private ResponseEntity<ApiErrorResponse> handleError(Exception ex, HttpStatus status, String description) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(
            description,
            Integer.toString(status.value()),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        ));
    }

    @ExceptionHandler(value = {LinkAlreadyTrackingException.class})
    public ResponseEntity<ApiErrorResponse> handleLinkAlreadyTracking(Exception ex) {
        return handleError(ex, HttpStatus.CONFLICT, "Link is already tracking");
    }

    @ExceptionHandler(value = {ChatAlreadyRegisteredException.class})
    public ResponseEntity<ApiErrorResponse> handleChatAlreadyRegistered(Exception ex) {
        return handleError(ex, HttpStatus.CONFLICT, "Chat is already registered");
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiErrorResponse> handleBadRequest(Exception ex) {
        return handleError(ex, HttpStatus.BAD_REQUEST, "Invalid request parameters");
    }

    @ExceptionHandler(value = {InvalidLinkException.class})
    public ResponseEntity<ApiErrorResponse> handleInvalidLink(Exception ex) {
        return handleError(ex, HttpStatus.BAD_REQUEST, "The link is not correct");
    }

    @ExceptionHandler(value = {NoSuchChatException.class})
    public ResponseEntity<ApiErrorResponse> handleNotFoundChat(Exception ex) {
        return handleError(ex, HttpStatus.NOT_FOUND, "Chat doesn't exist");
    }

    @ExceptionHandler(value = {NoSuchLinkException.class})
    public ResponseEntity<ApiErrorResponse> handleNotFoundLink(Exception ex) {
        return handleError(ex, HttpStatus.NOT_FOUND, "The link is not tracked by this chat");
    }
}
