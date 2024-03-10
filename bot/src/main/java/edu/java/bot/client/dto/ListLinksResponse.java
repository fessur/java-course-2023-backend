package edu.java.bot.client.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ListLinksResponse {
    private List<LinkResponse> links;
    private long size;
}
