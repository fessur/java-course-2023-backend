package edu.java.configuration;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull
    @Bean
    Scheduler scheduler,

    @NotNull
    Clients clients,

    @NotNull
    RateLimit rateLimit,

    @NotNull
    AccessType databaseAccessType
) {
    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public record Clients(@NotNull Github github,
                          @NotNull StackOverflow stackOverflow,
                          @NotNull TrackerBot trackerBot) {
    }

    public record Github(@NotEmpty String baseUrl, @NotNull Retry retry) {
    }

    public record StackOverflow(@NotEmpty String baseUrl, @NotNull Retry retry) {
    }

    public record TrackerBot(@NotEmpty String baseUrl, @NotNull Retry retry) {
    }

    @Getter
    @Setter
    public static class Retry {
        // Common properties
        @NotNull
        private RetryPolicy policy;
        @NotNull
        private int[] statusCodes;
        private Integer maxAttempts;
        // Constant
        private Duration step;
        // Linear
        private Duration initialInterval; // also used for exponent
        private Duration increment;
        private Duration maxInterval; // also used for exponent
        // Exponent
        private Double multiplier;
    }

    public enum RetryPolicy {
        CONSTANT, LINEAR, EXPONENT
    }

    public record RateLimit(@Positive int requests, @NotNull Duration interval) {
    }

    public enum AccessType {
        JDBC, JPA,
    }
}
