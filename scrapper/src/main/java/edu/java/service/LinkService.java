package edu.java.service;

import edu.java.client.dto.GithubRepositoryRequest;
import edu.java.repository.dto.Link;
import java.net.URL;
import java.util.Collection;

public interface LinkService {
    Link add(String url, long chatId);
    Link remove(String url, long chatId);
    Collection<Link> listAll(long chatId);

    static GithubRepositoryRequest toGithubRepository(URL url) {
        String[] parts = url.getPath().split("/");
        return new GithubRepositoryRequest(parts[1], parts[2]);
    }

    static long toStackOverflowQuestion(URL url) {
        return Long.parseLong(url.getPath().split("/")[2]);
    }
}
