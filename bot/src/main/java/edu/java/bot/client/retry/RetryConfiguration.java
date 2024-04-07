package edu.java.bot.client.retry;

import edu.java.bot.configuration.props.ApplicationConfig;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

@Configuration
@Slf4j
public class RetryConfiguration {
    @Bean
    public Retry retrySpec(ApplicationConfig applicationConfig) {
        Set<Integer> statusCodes = Arrays.stream(applicationConfig.retry().getStatusCodes())
            .boxed().collect(Collectors.toSet());
        Predicate<Throwable> filter =
            throwable -> throwable instanceof WebClientResponseException webClientResponseException
                && statusCodes.contains(webClientResponseException.getStatusCode().value());
        return switch (applicationConfig.retry().getPolicy()) {
            case CONSTANT ->
                Retry.fixedDelay(applicationConfig.retry().getMaxAttempts(), applicationConfig.retry().getStep())
                    .filter(filter)
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure());
            case EXPONENT -> Retry.backoff(
                    applicationConfig.retry().getMaxAttempts(),
                    applicationConfig.retry().getInitialInterval()
                )
                .filter(filter)
                .maxBackoff(applicationConfig.retry().getMaxInterval())
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure());
            case LINEAR -> new LinearRetry(
                applicationConfig.retry().getMaxAttempts(),
                applicationConfig.retry().getInitialInterval(),
                applicationConfig.retry().getIncrement(),
                filter,
                applicationConfig.retry().getMaxInterval(),
                (retryBackoffSpec, retrySignal) -> retrySignal.failure()
            );

        };
    }
}
