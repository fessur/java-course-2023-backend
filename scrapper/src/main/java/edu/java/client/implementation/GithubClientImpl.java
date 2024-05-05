package edu.java.client.implementation;

import edu.java.client.GithubClient;
import edu.java.client.dto.GithubRepositoryRequest;
import edu.java.client.dto.GithubRepositoryResponse;
import java.util.Optional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class GithubClientImpl implements GithubClient {
    private final WebClient webClient;
    private final Retry retrySpec;
    private final static String URI_PATTERN = "/repos/{owner}/{repo}";

    public GithubClientImpl(String baseUrl, Retry retrySpec) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.retrySpec = retrySpec;
    }

    @Override
    public Optional<GithubRepositoryResponse> fetchRepository(GithubRepositoryRequest request) {
        try {
            return webClient.get()
                .uri(URI_PATTERN, request.owner(), request.repo())
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.createException().flatMap(Mono::error);
                    }
                    return response.bodyToMono(GithubRepositoryResponse.class)
                        .flatMap(r -> Mono.just(Optional.of(r)));
                })
                .retryWhen(retrySpec)
                .block();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean exists(GithubRepositoryRequest request) {
        try {
            return Boolean.TRUE.equals(webClient.get()
                .uri(URI_PATTERN, request.owner(), request.repo())
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.createException().flatMap(Mono::error);
                    } else {
                        return Mono.just(true);
                    }
                })
                .retryWhen(retrySpec)
                .block());
        } catch (Exception ex) {
            return false;
        }
    }
}
