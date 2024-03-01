package edu.java.client;

import edu.java.client.implementation.GithubClientImpl;
import edu.java.client.implementation.StackOverflowClientImpl;
import edu.java.client.implementation.TrackerBotClientImpl;
import edu.java.configuration.ApplicationConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {
    @Bean
    public GithubClient githubClient(
        ApplicationConfig applicationConfig
    ) {
        return new GithubClientImpl(applicationConfig.baseUrl().github());
    }

    @Bean
    public StackOverflowClient stackOverflowClient(
        ApplicationConfig applicationConfig
    ) {
        return new StackOverflowClientImpl(applicationConfig.baseUrl().stackoverflow());
    }

    @Bean
    public TrackerBotClient trackerBotClient(
        ApplicationConfig applicationConfig
    ) {
        return new TrackerBotClientImpl(applicationConfig.baseUrl().trackerBot());
    }
}
