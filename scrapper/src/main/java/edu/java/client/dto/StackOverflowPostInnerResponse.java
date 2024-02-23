package edu.java.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.Getter;

@Getter
public class StackOverflowPostInnerResponse {
    protected final long id;
    protected final String title;
    protected final OffsetDateTime lastActivityDate;

    @JsonCreator
    StackOverflowPostInnerResponse(
        @JsonProperty("post_id") long id,
        @JsonProperty("title") String title,
        @JsonProperty("last_activity_date") OffsetDateTime lastActivityDate) {
        this.id = id;
        this.title = title;
        this.lastActivityDate = lastActivityDate;
    }
}
