package edu.java.client.implementation;

import edu.java.client.StackOverflowClient;
import edu.java.client.dto.StackOverflowPostResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class StackOverflowClientImpl implements StackOverflowClient {
    private final WebClient webClient;

    public StackOverflowClientImpl(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public StackOverflowPostResponse fetchPost(long postId) {
        return webClient.get()
            .uri("/posts/{postId}?site=stackoverflow&filter=!nNPvSNOTRz", postId)
            .retrieve()
            .bodyToMono(StackOverflowPostResponse.class)
            .block();
    }

    @Override
    public boolean exists(long postId) {
        return Boolean.TRUE.equals(webClient.get()
            .uri("/posts/{postId}?site=stackoverflow&filter=!nNPvSNOTRz", postId)
            .retrieve()
            .bodyToMono(StackOverflowPostResponse.class)
            .flatMap(response -> {
                if (response != null && !response.items().isEmpty()) {
                    return Mono.just(true);
                }
                return Mono.just(false);
            }).block());
    }
}
