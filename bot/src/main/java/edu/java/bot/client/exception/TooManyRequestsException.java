package edu.java.bot.client.exception;

import edu.java.bot.client.dto.ApiErrorResponse;

public class TooManyRequestsException extends ApiErrorResponseException {
    public TooManyRequestsException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse);
    }
}
