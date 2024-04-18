package edu.java.client;

import edu.java.client.dto.GithubRepositoryRequest;
import edu.java.client.dto.GithubRepositoryResponse;
import java.util.Optional;

public interface GithubClient {
    Optional<GithubRepositoryResponse> fetchRepository(GithubRepositoryRequest githubRepositoryRequest);

    boolean exists(GithubRepositoryRequest githubRepositoryRequest);
}
