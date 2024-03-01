package edu.java.client.exception;

import edu.java.controller.dto.ApiErrorResponse;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiErrorResponseException extends RuntimeException {
    protected final String description;
    protected final String code;
    protected final String exceptionName;
    protected final String exceptionMessage;
    protected final List<String> stacktrace;

    public ApiErrorResponseException(ApiErrorResponse apiErrorResponse) {
        this.description = apiErrorResponse.description();
        this.code = apiErrorResponse.code();
        this.exceptionName = apiErrorResponse.exceptionName();
        this.exceptionMessage = apiErrorResponse.exceptionMessage();
        this.stacktrace = apiErrorResponse.stacktrace();
    }
}
