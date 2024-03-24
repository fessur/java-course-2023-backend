package edu.java.service.model;

import java.time.OffsetDateTime;

public interface Link {
    long getId();

    String getUrl();

    OffsetDateTime getLastCheckTime();

    OffsetDateTime getCreatedAt();
}
