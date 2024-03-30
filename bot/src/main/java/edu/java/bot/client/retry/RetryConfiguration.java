package edu.java.bot.client.retry;

import edu.java.bot.configuration.ApplicationConfig;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfiguration {
    @Bean
    public RetryTemplate retryTemplate(ApplicationConfig applicationConfig) {
        Set<HttpStatusCode> statusCodes = Arrays.stream(applicationConfig.retry().getStatusCodes())
            .mapToObj(HttpStatusCode::valueOf).collect(Collectors.toSet());
        RetryPolicy retryPolicy = applicationConfig.retry().getMaxAttempts() == null
            ? new StatusCodeRetryPolicy(statusCodes)
            : new StatusCodeRetryPolicy(applicationConfig.retry().getMaxAttempts(), statusCodes);

        BackOffPolicy backOffPolicy = switch (applicationConfig.retry().getPolicy()) {
            case CONSTANT -> {
                FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
                fixedBackOffPolicy.setBackOffPeriod(applicationConfig.retry().getStep().toMillis());
                yield fixedBackOffPolicy;
            }
            case LINEAR -> {
                LinearBackOffPolicy linearBackOffPolicy = new LinearBackOffPolicy();
                linearBackOffPolicy.setInitialInterval(applicationConfig.retry().getInitialInterval().toMillis());
                linearBackOffPolicy.setIncrement(applicationConfig.retry().getIncrement().toMillis());
                linearBackOffPolicy.setMaxInterval(applicationConfig.retry().getMaxInterval().toMillis());
                yield linearBackOffPolicy;
            }
            case EXPONENT -> {
                ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
                exponentialBackOffPolicy.setInitialInterval(applicationConfig.retry().getInitialInterval().toMillis());
                exponentialBackOffPolicy.setMultiplier(applicationConfig.retry().getMultiplier());
                exponentialBackOffPolicy.setMaxInterval(applicationConfig.retry().getMaxInterval().toMillis());
                yield exponentialBackOffPolicy;
            }
        };
        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(backOffPolicy);
        return template;
    }
}
