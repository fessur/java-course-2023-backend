package edu.java.client.retry;

import edu.java.configuration.props.ApplicationConfig;
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
    public Retry githubRetrySpec(ApplicationConfig applicationConfig) {
        return createRetrySpec(applicationConfig.clients().github().retry());
    }

    @Bean
    public Retry stackOverflowRetrySpec(ApplicationConfig applicationConfig) {
        return createRetrySpec(applicationConfig.clients().stackOverflow().retry());
    }

    public Retry createRetrySpec(ApplicationConfig.Retry retryConfig) {
        Set<Integer> statusCodes = Arrays.stream(retryConfig.getStatusCodes())
            .boxed().collect(Collectors.toSet());
        Predicate<Throwable> filter =
            throwable -> throwable instanceof WebClientResponseException webClientResponseException
                && statusCodes.contains(webClientResponseException.getStatusCode().value());
        return switch (retryConfig.getPolicy()) {
            case CONSTANT ->
                Retry.fixedDelay(retryConfig.getMaxAttempts(), retryConfig.getStep())
                    .filter(filter)
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure());
            case EXPONENT ->
                Retry.backoff(retryConfig.getMaxAttempts(), retryConfig.getInitialInterval())
                    .filter(filter)
                    .maxBackoff(retryConfig.getMaxInterval())
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure());
            case LINEAR ->
                new LinearRetry(
                    retryConfig.getMaxAttempts(),
                    retryConfig.getInitialInterval(),
                    retryConfig.getIncrement(),
                    filter,
                    retryConfig.getMaxInterval(),
                    (retryBackoffSpec, retrySignal) -> retrySignal.failure()
                );
        };
    }
}
