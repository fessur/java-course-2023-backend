package edu.java.bot.configuration.props;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "kafka.consumer", ignoreUnknownFields = false)
public record KafkaConsumerProperties(
    @NotEmpty
    String bootstrapServers,

    @NotEmpty
    String groupId,

    @NotEmpty
    String autoOffsetReset,

    @Positive
    Integer maxPollIntervalMs,

    boolean enableAutoCommit,

    @Positive
    Integer concurrency,

    @NotNull
    Backoff backoff
) {
    public record Backoff(@NotNull Duration interval, @Positive int maxAttempts) {
    }
}
