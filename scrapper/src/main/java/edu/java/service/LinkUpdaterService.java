package edu.java.service;

import edu.java.client.dto.GithubRepositoryRequest;
import java.net.URL;
import java.util.Optional;

public interface LinkUpdaterService {
    int update();

    Optional<String> validateLink(URL url);

    static String normalizeLink(URL url) {
        return switch (url.getHost()) {
            case "github.com" -> {
                GithubRepositoryRequest request = toGithubRepository(url);
                yield "https://github.com/" + request.owner() + "/" + request.repo();
            }
            case "stackoverflow.com" -> "https://stackoverflow.com/questions/" + toStackOverflowQuestion(url);
            default -> throw new IllegalArgumentException("The domain is not supported");
        };
    }

    static GithubRepositoryRequest toGithubRepository(URL url) {
        String[] parts = url.getPath().split("/");
        return new GithubRepositoryRequest(parts[1], parts[2]);
    }

    static long toStackOverflowQuestion(URL url) {
        return Long.parseLong(url.getPath().split("/")[2]);
    }
}
