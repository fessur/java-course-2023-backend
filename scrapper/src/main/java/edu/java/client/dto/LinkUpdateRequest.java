package edu.java.client.dto;

import java.util.Collection;

public record LinkUpdateRequest(long id, String url, String description, Collection<Long> tgChatIds) {
}
