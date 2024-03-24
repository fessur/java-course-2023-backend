package edu.java.service.site;

import edu.java.client.GithubClient;
import edu.java.client.dto.GithubRepositoryRequest;
import edu.java.client.dto.GithubRepositoryResponse;
import java.net.URL;

public abstract class Github implements Site {
    private static final String NAME = "github.com";
    protected final GithubClient githubClient;

    protected Github(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    @Override
    public boolean isValid(URL url) {
        String[] parts = url.getPath().split("/");
        return url.getProtocol().equals("https")
            && url.getHost().equals(NAME)
            && parts.length >= 2
            && !parts[1].isEmpty()
            && !parts[2].isEmpty();
    }

    @Override
    public boolean exists(URL url) {
        return githubClient.exists(toGithubRepository(url));
    }

    @Override
    public String notExistsMessage() {
        return "Cannot find such repository.";
    }

    @Override
    public String normalize(URL url) {
        GithubRepositoryRequest request = toGithubRepository(url);
        return "https://github.com/" + request.owner() + "/" + request.repo();
    }

    @Override
    public String toString() {
        return NAME;
    }

    protected GithubRepositoryRequest toGithubRepository(URL url) {
        String[] parts = url.getPath().split("/");
        return new GithubRepositoryRequest(parts[1], parts[2]);
    }

    protected String createDescription(GithubRepositoryResponse response) {
        return "Repository " + response.name();
    }
}
