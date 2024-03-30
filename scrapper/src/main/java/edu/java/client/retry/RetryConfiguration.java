package edu.java.client.retry;

import edu.java.configuration.ApplicationConfig;
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
    public RetryTemplate githubRetryTemplate(ApplicationConfig applicationConfig) {
        return createRetryTemplate(applicationConfig.clients().github().retry());
    }

    @Bean
    public RetryTemplate stackOverflowRetryTemplate(ApplicationConfig applicationConfig) {
        return createRetryTemplate(applicationConfig.clients().stackOverflow().retry());
    }

    @Bean
    public RetryTemplate trackerBotRetryTemplate(ApplicationConfig applicationConfig) {
        return createRetryTemplate(applicationConfig.clients().trackerBot().retry());
    }

    private RetryTemplate createRetryTemplate(ApplicationConfig.Retry retry) {
        Set<HttpStatusCode> statusCodes = Arrays.stream(retry.getStatusCodes())
            .mapToObj(HttpStatusCode::valueOf).collect(Collectors.toSet());
        RetryPolicy retryPolicy = retry.getMaxAttempts() == null
            ? new StatusCodeRetryPolicy(statusCodes)
            : new StatusCodeRetryPolicy(retry.getMaxAttempts(), statusCodes);

        BackOffPolicy backOffPolicy = switch (retry.getPolicy()) {
            case CONSTANT -> {
                FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
                fixedBackOffPolicy.setBackOffPeriod(retry.getStep().toMillis());
                yield fixedBackOffPolicy;
            }
            case LINEAR -> {
                LinearBackOffPolicy linearBackOffPolicy = new LinearBackOffPolicy();
                linearBackOffPolicy.setInitialInterval(retry.getInitialInterval().toMillis());
                linearBackOffPolicy.setIncrement(retry.getIncrement().toMillis());
                linearBackOffPolicy.setMaxInterval(retry.getMaxInterval().toMillis());
                yield linearBackOffPolicy;
            }
            case EXPONENT -> {
                ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
                exponentialBackOffPolicy.setInitialInterval(retry.getInitialInterval().toMillis());
                exponentialBackOffPolicy.setMultiplier(retry.getMultiplier());
                exponentialBackOffPolicy.setMaxInterval(retry.getMaxInterval().toMillis());
                yield exponentialBackOffPolicy;
            }
        };
        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(backOffPolicy);
        return template;
    }
}
