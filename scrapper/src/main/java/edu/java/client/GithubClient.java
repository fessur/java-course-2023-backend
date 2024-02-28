package edu.java.client;

import edu.java.client.dto.GithubRepositoryResponse;

public interface GithubClient {
    GithubRepositoryResponse fetchRepository(String owner, String repo);
}
