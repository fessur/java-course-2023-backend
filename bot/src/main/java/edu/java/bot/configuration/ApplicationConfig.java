package edu.java.bot.configuration;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty
    String telegramToken,

    @NotEmpty
    String scrapperBaseUrl,

    @NotNull
    Retry retry
) {
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
}
