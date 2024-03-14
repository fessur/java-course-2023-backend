package edu.java.bot.controller.dto;

import edu.java.bot.controller.validation.annotation.CorrectLink;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkUpdateRequest {
    private long id;
    @CorrectLink
    private String url;
    private String description;
    private List<Long> tgChatIds;
}
