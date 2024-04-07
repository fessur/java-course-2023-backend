package edu.java.gateway.dto;

import java.util.Collection;

public record LinkUpdate(long id, String url, String description, Collection<Long> tgChatIds) {
}
