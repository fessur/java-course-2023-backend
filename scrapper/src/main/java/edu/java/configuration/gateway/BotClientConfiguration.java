package edu.java.configuration.gateway;

import edu.java.client.retry.RetryConfiguration;
import edu.java.configuration.props.ApplicationConfig;
import edu.java.gateway.TrackerBotClient;
import edu.java.gateway.UpdatesGateway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.retry.Retry;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "false")
public class BotClientConfiguration {
    @Bean
    public Retry trackerBotRetrySpec(
        ApplicationConfig applicationConfig,
        RetryConfiguration retryConfiguration
    ) {
        return retryConfiguration.createRetrySpec(applicationConfig.clients().trackerBot().retry());
    }

    @Bean
    public UpdatesGateway trackerBotClient(
        ApplicationConfig applicationConfig, @Qualifier("trackerBotRetrySpec") Retry retrySpec
    ) {
        return new TrackerBotClient(applicationConfig.clients().trackerBot().baseUrl(), retrySpec);
    }
}
