package edu.java.client.implementation;

import edu.java.client.StackOverflowClient;
import edu.java.client.dto.StackOverflowPostInnerResponse;
import edu.java.client.dto.StackOverflowPostResponse;
import java.util.Optional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class StackOverflowClientImpl implements StackOverflowClient {
    private final WebClient webClient;
    private final Retry retrySpec;
    private static final String URI_PATTERN = "/posts/{postId}?site=stackoverflow&filter=!nNPvSNOTRz";

    public StackOverflowClientImpl(String baseUrl, Retry retrySpec) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.retrySpec = retrySpec;
    }

    @Override
    public Optional<StackOverflowPostInnerResponse> fetchPost(long postId) {
        try {
            Optional<StackOverflowPostResponse> resp = webClient.get()
                .uri(URI_PATTERN, postId)
                .exchangeToMono(response -> {
                    if (response.statusCode().is4xxClientError() || response.statusCode().is5xxServerError()) {
                        return response.createException().flatMap(Mono::error);
                    }
                    return response.bodyToMono(StackOverflowPostResponse.class).flatMap(r -> Mono.just(Optional.of(r)));
                })
                .retryWhen(retrySpec)
                .block();
            if (resp.isEmpty() || resp.get().items().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(resp.get().items().getFirst());
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    @Override
    public boolean exists(long postId) {
        try {
            return Boolean.TRUE.equals(webClient.get()
                .uri(URI_PATTERN, postId)
                .retrieve()
                .bodyToMono(StackOverflowPostResponse.class)
                .flatMap(response -> {
                    if (response != null && !response.items().isEmpty()) {
                        return Mono.just(true);
                    }
                    return Mono.just(false);
                })
                .retryWhen(retrySpec)
                .block());
        } catch (Exception ex) {
            return false;
        }
    }
}
