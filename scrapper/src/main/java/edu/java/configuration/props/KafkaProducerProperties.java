package edu.java.configuration.props;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


@Validated
@ConfigurationProperties(prefix = "kafka.producer", ignoreUnknownFields = false)
public record KafkaProducerProperties(
    @NotEmpty
    String bootstrapServers,

    @NotEmpty
    String clientId,

    @NotEmpty
    String acksMode,

    @NotNull
    Duration deliveryTimeout,

    @Positive
    Integer lingerMs,

    @Positive
    Integer batchSize,

    @Positive
    Integer maxInFlightPerConnection,

    @NotNull
    Boolean enableIdempotence
) {
}
