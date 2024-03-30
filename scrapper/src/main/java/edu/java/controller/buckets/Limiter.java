package edu.java.controller.buckets;

import edu.java.configuration.ApplicationConfig;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class Limiter {
    private final Map<String, Bucket> bucketMap;
    private final int requests;
    private final Duration interval;

    public Limiter(ApplicationConfig applicationConfig) {
        this.bucketMap = new HashMap<>();
        this.requests = applicationConfig.rateLimit().requests();
        this.interval = applicationConfig.rateLimit().interval();
    }

    public boolean tryConsume(String ip) {
        bucketMap.putIfAbsent(ip, Bucket.builder()
            .addLimit(limit -> limit.capacity(requests).refillGreedy(requests, interval))
            .build());
        return bucketMap.get(ip).tryConsume(1);
    }
}
