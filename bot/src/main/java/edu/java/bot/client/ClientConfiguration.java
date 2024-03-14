package edu.java.bot.client;

import edu.java.bot.client.implementation.ScrapperClientImpl;
import edu.java.bot.configuration.ApplicationConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {
    @Bean
    public ScrapperClient scrapperClient(ApplicationConfig applicationConfig) {
        return new ScrapperClientImpl(applicationConfig.scrapperBaseUrl());
    }
}
