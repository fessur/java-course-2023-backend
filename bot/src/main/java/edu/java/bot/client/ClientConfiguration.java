package edu.java.bot.client;

import edu.java.bot.client.implementation.ScrapperClientImpl;
import edu.java.bot.configuration.props.ApplicationConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.retry.Retry;

@Configuration
public class ClientConfiguration {
    @Bean
    public ScrapperClient scrapperClient(ApplicationConfig applicationConfig, Retry retrySpec) {
        return new ScrapperClientImpl(applicationConfig.scrapperBaseUrl(), retrySpec);
    }
}
