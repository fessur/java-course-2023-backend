package edu.java.client;

import edu.java.client.implementation.GithubClientImpl;
import edu.java.client.implementation.StackOverflowClientImpl;
import edu.java.configuration.props.ApplicationConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.retry.Retry;

@Configuration
public class ClientConfiguration {
    @Bean
    public GithubClient githubClient(
        ApplicationConfig applicationConfig, @Qualifier("githubRetrySpec") Retry retrySpec
    ) {
        return new GithubClientImpl(applicationConfig.clients().github().baseUrl(), retrySpec);
    }

    @Bean
    public StackOverflowClient stackOverflowClient(
        ApplicationConfig applicationConfig, @Qualifier("stackOverflowRetrySpec") Retry retrySpec
    ) {
        return new StackOverflowClientImpl(applicationConfig.clients().stackOverflow().baseUrl(), retrySpec);
    }
}
