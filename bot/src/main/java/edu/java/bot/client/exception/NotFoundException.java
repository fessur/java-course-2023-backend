package edu.java.bot.client.exception;

import edu.java.bot.client.dto.ApiErrorResponse;

public class NotFoundException extends ApiErrorResponseException {
    public NotFoundException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse);
    }
}
