package edu.java.client.retry;

import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class LinearRetry extends Retry {
    private final int maxAttempts;
    private final Duration initialInterval;
    private final Duration increment;
    private final Predicate<Throwable> filter;
    private final Duration maxInterval;
    private final BiFunction<LinearRetry, RetrySignal, Throwable> retryExhaustedGenerator;

    public LinearRetry(
        int maxAttempts,
        Duration initialInterval,
        Duration increment,
        Predicate<Throwable> filter,
        Duration maxInterval,
        BiFunction<LinearRetry, RetrySignal, Throwable> retryExhaustedGenerator
    ) {
        this.maxAttempts = maxAttempts;
        this.initialInterval = initialInterval;
        this.increment = increment;
        this.filter = filter;
        this.maxInterval = maxInterval;
        this.retryExhaustedGenerator = retryExhaustedGenerator;
    }

    @Override
    public Publisher<?> generateCompanion(Flux<RetrySignal> flux) {
        return flux.flatMap(this::createRetry);
    }

    private Mono<Long> createRetry(RetrySignal rs) {
        RetrySignal copy = rs.copy();

        if (!this.filter.test(rs.failure())) {
            return Mono.error(rs.failure());
        }

        if (rs.totalRetries() < maxAttempts) {
            Duration expectedInterval = initialInterval.plus(increment.multipliedBy(rs.totalRetries()));
            Duration interval = maxInterval.compareTo(expectedInterval) <= 0 ? maxInterval : expectedInterval;
            return Mono.delay(interval).thenReturn(rs.totalRetries());
        } else {
            return Mono.error(this.retryExhaustedGenerator.apply(this, copy));
        }
    }
}
