package edu.java.bot.controller.dto;

import edu.java.bot.controller.validation.annotation.CorrectLink;
import java.util.List;

public record LinkUpdateRequest(long id, @CorrectLink String url, String description, List<Long> tgChatIds) {
}
