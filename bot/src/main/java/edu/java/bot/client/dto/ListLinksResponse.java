package edu.java.bot.client.dto;

import java.util.List;

public record ListLinksResponse(List<LinkResponse> links, long size) {
}
