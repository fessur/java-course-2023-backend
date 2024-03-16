package edu.java.client.implementation;

import edu.java.client.GithubClient;
import edu.java.client.dto.GithubRepositoryRequest;
import edu.java.client.dto.GithubRepositoryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GithubClientImpl implements GithubClient {
    private final WebClient webClient;

    public GithubClientImpl(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public GithubRepositoryResponse fetchRepository(GithubRepositoryRequest request) {
        return webClient.get()
            .uri("/repos/{owner}/{repo}", request.owner(), request.repo())
            .retrieve()
            .bodyToMono(GithubRepositoryResponse.class)
            .block();
    }

    @Override
    public boolean exists(GithubRepositoryRequest request) {
        return Boolean.TRUE.equals(webClient.get()
            .uri("/repos/{owner}/{repo}", request.owner(), request.repo())
            .exchangeToMono(response -> {
                if (response.statusCode().is4xxClientError()) {
                    return Mono.just(false);
                } else {
                    return Mono.just(true);
                }
            })
            .block());
    }
}
