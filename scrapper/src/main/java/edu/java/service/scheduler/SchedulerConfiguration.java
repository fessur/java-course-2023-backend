package edu.java.service.scheduler;

import edu.java.service.LinkUpdaterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfiguration {
    @Bean
    public LinkUpdaterScheduler linkUpdaterScheduler(LinkUpdaterService linkUpdaterService) {
        return new LinkUpdaterScheduler(linkUpdaterService);
    }
}
