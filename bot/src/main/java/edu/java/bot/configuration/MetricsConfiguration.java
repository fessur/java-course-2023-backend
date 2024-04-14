package edu.java.bot.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfiguration {
    @Bean
    public Counter processedMessages(MeterRegistry registry) {
        return Counter
            .builder("processed.messages")
            .description("counts number of users' messages processed")
            .register(registry);
    }
}
