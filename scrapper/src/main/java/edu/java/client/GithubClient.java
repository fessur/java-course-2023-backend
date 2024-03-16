package edu.java.client;

import edu.java.client.dto.GithubRepositoryRequest;
import edu.java.client.dto.GithubRepositoryResponse;

public interface GithubClient {
    GithubRepositoryResponse fetchRepository(GithubRepositoryRequest githubRepositoryRequest);
    boolean exists(GithubRepositoryRequest githubRepositoryRequest);
}
