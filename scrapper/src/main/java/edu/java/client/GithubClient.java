package edu.java.client;

import edu.java.client.dto.GithubRepositoryResponse;
import reactor.core.publisher.Mono;

public interface GithubClient {
    Mono<GithubRepositoryResponse> fetchRepository(String owner, String repo);
}
