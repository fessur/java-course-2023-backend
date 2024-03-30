package edu.java.controller;

import edu.java.controller.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public abstract class BaseController {
    protected ResponseEntity<ApiErrorResponse> createBadRequestResponse(BindingResult bindingResult) {
        return ResponseEntity.badRequest()
            .body(new ApiErrorResponse(
                bindingResult.getAllErrors().getFirst().getDefaultMessage(),
                Integer.toString(HttpStatus.BAD_REQUEST.value())
            ));
    }

    protected ResponseEntity<ApiErrorResponse> createTooManyRequests() {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .body(new ApiErrorResponse(
                "Too many requests.",
                Integer.toString(HttpStatus.TOO_MANY_REQUESTS.value())
            ));
    }
}
