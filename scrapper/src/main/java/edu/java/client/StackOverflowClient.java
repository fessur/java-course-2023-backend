package edu.java.client;

import edu.java.client.dto.StackOverflowPostResponse;
import java.util.Optional;

public interface StackOverflowClient {
    Optional<StackOverflowPostResponse> fetchPost(long id);
    boolean exists(long id);
}
