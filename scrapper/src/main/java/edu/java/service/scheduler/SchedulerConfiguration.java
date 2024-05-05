package edu.java.service.scheduler;

import edu.java.service.LinkUpdaterService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "app", name = "scheduler.enable", havingValue = "true")
public class SchedulerConfiguration {
    @Bean
    public LinkUpdaterScheduler linkUpdaterScheduler(LinkUpdaterService linkUpdaterService) {
        return new LinkUpdaterScheduler(linkUpdaterService);
    }
}
