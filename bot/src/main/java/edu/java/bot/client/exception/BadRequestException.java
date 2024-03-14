package edu.java.bot.client.exception;

import edu.java.bot.client.dto.ApiErrorResponse;

public class BadRequestException extends ApiErrorResponseException {

    public BadRequestException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse);
    }
}
