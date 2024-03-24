package edu.java.service.site.jpa;

import edu.java.client.GithubClient;
import edu.java.client.TrackerBotClient;
import edu.java.service.model.jpa.JpaChat;
import edu.java.service.model.jpa.JpaLink;
import edu.java.service.site.Github;
import edu.java.util.CommonUtils;
import java.net.URL;

public class JpaGithub extends Github implements JpaSite {
    private final TrackerBotClient trackerBotClient;

    public JpaGithub(GithubClient githubClient, TrackerBotClient trackerBotClient) {
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
                        "Repository " + githubResponse.name(),
                        link.getChats().stream().map(JpaChat::getId).toList()
                    );
                }
            }));
    }
}
