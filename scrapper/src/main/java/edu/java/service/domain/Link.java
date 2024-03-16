package edu.java.service.domain;

import java.time.OffsetDateTime;

public record Link(long id, String url, OffsetDateTime lastCheckTime, OffsetDateTime createdAt) {
}
