package edu.java.service.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public class LinkUpdaterScheduler {
    @Scheduled(fixedDelayString = "#{scheduler.interval}")
    public void update() {
        log.info("Updating...");
    }
}
