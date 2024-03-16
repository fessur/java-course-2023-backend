package edu.java.repository.dto;

import java.time.OffsetDateTime;

public record Link(long id, String url, OffsetDateTime lastCheckTime, OffsetDateTime createdAt) {
}
