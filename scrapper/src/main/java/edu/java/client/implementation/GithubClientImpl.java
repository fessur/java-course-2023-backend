package edu.java.client.implementation;

import edu.java.client.GithubClient;
import edu.java.client.dto.GithubRepositoryResponse;
import org.springframework.web.reactive.function.client.WebClient;

public class GithubClientImpl implements GithubClient {
    private final WebClient webClient;

    public GithubClientImpl(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public GithubRepositoryResponse fetchRepository(String owner, String repo) {
        return webClient.get()
            .uri("/repos/{owner}/{repo}", owner, repo)
            .retrieve()
            .bodyToMono(GithubRepositoryResponse.class)
            .block();
    }
}
