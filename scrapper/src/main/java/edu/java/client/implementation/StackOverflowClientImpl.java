package edu.java.client.implementation;

import edu.java.client.StackOverflowClient;
import edu.java.client.dto.StackOverflowPostResponse;
import org.springframework.web.reactive.function.client.WebClient;

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
}
