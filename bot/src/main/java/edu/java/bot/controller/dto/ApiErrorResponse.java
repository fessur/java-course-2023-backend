package edu.java.bot.controller.dto;

import java.util.List;

public record ApiErrorResponse(
    String description,
    String code,
    String exceptionName,
    String exceptionMessage,
    List<String> stacktrace
) {
    public ApiErrorResponse(String description, String code) {
        this(description, code, "", "", List.of());
    }
}
