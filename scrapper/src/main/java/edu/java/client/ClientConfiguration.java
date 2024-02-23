package edu.java.client;

import edu.java.client.implementation.GithubClientImpl;
import edu.java.client.implementation.StackOverflowClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfiguration {
    @Bean
    public GithubClient githubClient(
        @Value("https://api.github.com") String baseUrl
    ) {
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        return new GithubClientImpl(webClient);
    }

    @Bean
    public StackOverflowClient stackOverflowClient(
        @Value("https://api.stackexchange.com/2.3") String baseUrl
    ) {
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        return new StackOverflowClientImpl(webClient);
    }
}
