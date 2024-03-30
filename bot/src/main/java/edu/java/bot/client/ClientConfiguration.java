package edu.java.bot.client;

import edu.java.bot.client.implementation.ScrapperClientImpl;
import edu.java.bot.configuration.ApplicationConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class ClientConfiguration {
    @Bean
    public ScrapperClient scrapperClient(ApplicationConfig applicationConfig, RetryTemplate retryTemplate) {
        return new ScrapperClientImpl(applicationConfig.scrapperBaseUrl(), retryTemplate);
    }
}
