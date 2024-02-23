package edu.java.client.implementation;

import edu.java.client.GithubClient;
import edu.java.client.dto.GithubRepositoryResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GithubClientImpl implements GithubClient {
    private final WebClient webClient;

    public GithubClientImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<GithubRepositoryResponse> fetchRepository(String owner, String repo) {
        return webClient.get()
            .uri("/repos/{owner}/{repo}", owner, repo)
            .retrieve()
            .bodyToMono(GithubRepositoryResponse.class);
    }
}
