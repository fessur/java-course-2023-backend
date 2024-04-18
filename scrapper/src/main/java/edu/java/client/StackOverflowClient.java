package edu.java.client;

import edu.java.client.dto.StackOverflowPostInnerResponse;
import java.util.Optional;

public interface StackOverflowClient {
    Optional<StackOverflowPostInnerResponse> fetchPost(long id);

    boolean exists(long id);
}
