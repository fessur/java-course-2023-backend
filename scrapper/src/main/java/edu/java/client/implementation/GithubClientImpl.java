package edu.java.client.implementation;

import edu.java.client.GithubClient;
import edu.java.client.dto.GithubRepositoryRequest;
import edu.java.client.dto.GithubRepositoryResponse;
import java.util.Optional;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GithubClientImpl implements GithubClient {
    private final WebClient webClient;
    private final RetryTemplate retryTemplate;
    private final static String URI_PATTERN = "/repos/{owner}/{repo}";

    public GithubClientImpl(String baseUrl, RetryTemplate retryTemplate) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.retryTemplate = retryTemplate;
    }

    @Override
    public Optional<GithubRepositoryResponse> fetchRepository(GithubRepositoryRequest request) {
        try {
            return retryTemplate.execute(context ->
                webClient.get()
                    .uri(URI_PATTERN, request.owner(), request.repo())
                    .exchangeToMono(response -> {
                        if (response.statusCode().isError()) {
                            return response.createException().flatMap(Mono::error);
                        }
                        return response.bodyToMono(GithubRepositoryResponse.class)
                            .flatMap(r -> Mono.just(Optional.of(r)));
                    })
                    .block());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean exists(GithubRepositoryRequest request) {
        try {
            return Boolean.TRUE.equals(retryTemplate.execute(context -> webClient.get()
                .uri(URI_PATTERN, request.owner(), request.repo())
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.createException().flatMap(Mono::error);
                    } else {
                        return Mono.just(true);
                    }
                })
                .block()));
        } catch (Exception ex) {
            return false;
        }
    }
}
