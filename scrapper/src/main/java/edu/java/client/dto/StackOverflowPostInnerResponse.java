package edu.java.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record StackOverflowPostInnerResponse(long id, String title, OffsetDateTime lastActivityDate) {
    public StackOverflowPostInnerResponse(
        @JsonProperty("post_id") long id,
        @JsonProperty("title") String title,
        @JsonProperty("last_activity_date") OffsetDateTime lastActivityDate) {
        this.id = id;
        this.title = title;
        this.lastActivityDate = lastActivityDate;
    }
}
