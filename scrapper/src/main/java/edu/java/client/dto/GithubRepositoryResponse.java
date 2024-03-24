package edu.java.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record GithubRepositoryResponse(long id, String name, OffsetDateTime lastActivityDate) {
    public GithubRepositoryResponse(long id, String name, OffsetDateTime lastActivityDate) {
        this.id = id;
        this.name = name;
        this.lastActivityDate = lastActivityDate;
    }

    @JsonCreator
    public GithubRepositoryResponse(
        @JsonProperty("id") long id,
        @JsonProperty("full_name") String name,
        @JsonProperty("pushed_at") OffsetDateTime pushedAt,
        @JsonProperty("updated_at") OffsetDateTime updatedAt
    ) {
        this(id, name, pushedAt.isAfter(updatedAt) ? pushedAt : updatedAt);
    }
}
