package edu.java.bot.client.exception;

import edu.java.bot.client.dto.ApiErrorResponse;

public class ConflictException extends ApiErrorResponseException {
    public ConflictException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse);
    }
}
