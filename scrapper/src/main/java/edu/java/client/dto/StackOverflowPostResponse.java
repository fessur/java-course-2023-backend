package edu.java.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class StackOverflowPostResponse extends StackOverflowPostInnerResponse {
    @JsonCreator
    public StackOverflowPostResponse(
        @JsonProperty("items") List<StackOverflowPostInnerResponse> items
    ) {
        super(items.getFirst().id, items.getFirst().title, items.getFirst().lastActivityDate);
    }
}
