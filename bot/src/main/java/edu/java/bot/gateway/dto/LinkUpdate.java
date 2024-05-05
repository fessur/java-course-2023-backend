package edu.java.bot.gateway.dto;

import edu.java.bot.gateway.controller.validation.annotation.CorrectLink;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;

public record LinkUpdate(
    long id,
    @CorrectLink String url,
    @NotNull String description,
    @NotNull Collection<Long> tgChatIds
) {
}
