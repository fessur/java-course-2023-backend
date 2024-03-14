package edu.java.client.exception;

import edu.java.controller.dto.ApiErrorResponse;

public class BadRequestException extends ApiErrorResponseException {
    public BadRequestException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse);
    }
}
