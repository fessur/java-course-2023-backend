package edu.java.controller.buckets;

import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class Limiter {
    private final Map<String, Bucket> bucketMap;

    public Limiter() {
        this.bucketMap = new HashMap<>();
    }

    public boolean tryConsume(String ip) {
        bucketMap.putIfAbsent(ip, Bucket.builder()
            .addLimit(limit -> limit.capacity(1).refillGreedy(1, Duration.ofMinutes(1)))
            .build());
        return bucketMap.get(ip).tryConsume(1);
    }
}
