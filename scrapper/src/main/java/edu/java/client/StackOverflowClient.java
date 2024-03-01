package edu.java.client;

import edu.java.client.dto.StackOverflowPostResponse;

public interface StackOverflowClient {
    StackOverflowPostResponse fetchPost(long id);
}
