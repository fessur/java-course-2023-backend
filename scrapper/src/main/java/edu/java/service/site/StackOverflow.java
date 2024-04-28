package edu.java.service.site;

import edu.java.client.StackOverflowClient;
import edu.java.client.dto.StackOverflowPostInnerResponse;
import java.net.URL;

public abstract class StackOverflow implements Site {
    private static final String NAME = "stackoverflow.com";
    protected final StackOverflowClient stackOverflowClient;

    protected StackOverflow(StackOverflowClient stackOverflowClient) {
        this.stackOverflowClient = stackOverflowClient;
    }

    @Override
    public boolean isValid(URL url) {
        if (!url.getProtocol().equals("https") || !url.getHost().equals(NAME)) {
            return false;
        }

        String[] parts = url.getPath().split("/");
        if (parts.length < 2 || !parts[1].equals("questions")) {
            return false;
        }

        try {
            Long.parseLong(parts[2]);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean exists(URL url) {
        return stackOverflowClient.exists(toStackOverflowQuestion(url));
    }

    @Override
    public String notExistsMessage() {
        return "Cannot find such question.";
    }

    @Override
    public String normalize(URL url) {
        return "https://stackoverflow.com/questions/" + toStackOverflowQuestion(url);
    }

    @Override
    public String toString() {
        return NAME;
    }

    protected long toStackOverflowQuestion(URL url) {
        return Long.parseLong(url.getPath().split("/")[2]);
    }

    protected String createDescription(StackOverflowPostInnerResponse response) {
        return "Question " + response.title();
    }
}
