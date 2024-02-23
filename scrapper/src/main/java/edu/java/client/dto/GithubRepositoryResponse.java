package edu.java.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record GithubRepositoryResponse(long id, String name, OffsetDateTime lastActivityDate) {
    @JsonCreator
    public GithubRepositoryResponse(
        @JsonProperty("id") long id,
        @JsonProperty("full_name") String name,
        @JsonProperty("updated_at") OffsetDateTime lastActivityDate
    ) {
        this.id = id;
        this.name = name;
        this.lastActivityDate = lastActivityDate;
    }
}
