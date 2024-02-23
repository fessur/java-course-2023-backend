package edu.java.client;

import edu.java.client.dto.StackOverflowPostResponse;
import reactor.core.publisher.Mono;

public interface StackOverflowClient {
    Mono<StackOverflowPostResponse> fetchPost(long id);
}
