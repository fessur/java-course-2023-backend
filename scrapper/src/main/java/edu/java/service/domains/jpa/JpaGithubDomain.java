package edu.java.service.domains.jpa;

import edu.java.client.GithubClient;
import edu.java.client.TrackerBotClient;
import edu.java.service.domains.GithubDomain;
import edu.java.service.model.jpa.JpaChat;
import edu.java.service.model.jpa.JpaLink;
import edu.java.util.CommonUtils;
import java.net.URL;

public class JpaGithubDomain extends GithubDomain implements JpaDomain {
    private final TrackerBotClient trackerBotClient;

    public JpaGithubDomain(GithubClient githubClient, TrackerBotClient trackerBotClient) {
        super(githubClient);
        this.trackerBotClient = trackerBotClient;
    }

    @Override
    public void update(JpaLink link) {
        URL parsed = CommonUtils.toURL(link.getUrl());
        githubClient.fetchRepository(toGithubRepository(parsed))
            .ifPresent((githubResponse -> {
                if (githubResponse.lastActivityDate().isAfter(link.getLastCheckTime())) {
                    trackerBotClient.sendUpdate(
                        link,
                        createDescription(githubResponse),
                        link.getChats().stream().map(JpaChat::getId).toList()
                    );
                }
            }));
    }
}
