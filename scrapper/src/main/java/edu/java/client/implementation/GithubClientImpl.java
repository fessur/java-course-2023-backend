package edu.java.client.implementation;

import edu.java.client.GithubClient;
import edu.java.client.dto.GithubRepositoryRequest;
import edu.java.client.dto.GithubRepositoryResponse;
import java.util.Optional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GithubClientImpl implements GithubClient {
    private final WebClient webClient;
    private final static String URI_PATTERN = "/repos/{owner}/{repo}";

    public GithubClientImpl(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public Optional<GithubRepositoryResponse> fetchRepository(GithubRepositoryRequest request) {
        return webClient.get()
            .uri(URI_PATTERN, request.owner(), request.repo())
            .exchangeToMono(response -> {
                if (response.statusCode().is4xxClientError()) {
                    return Mono.just(Optional.<GithubRepositoryResponse>empty());
                }
                return response.bodyToMono(GithubRepositoryResponse.class).flatMap(r -> Mono.just(Optional.of(r)));
            })
            .block();
    }

    @Override
    public boolean exists(GithubRepositoryRequest request) {
        return Boolean.TRUE.equals(webClient.get()
            .uri(URI_PATTERN, request.owner(), request.repo())
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
