package edu.java.service.domains.jdbc;

import edu.java.client.GithubClient;
import edu.java.client.TrackerBotClient;
import edu.java.repository.jdbc.JdbcChatRepository;
import edu.java.service.domains.GithubDomain;
import edu.java.service.model.Chat;
import edu.java.service.model.jdbc.JdbcLink;
import edu.java.util.CommonUtils;
import java.net.URL;

public class JdbcGithubDomain extends GithubDomain implements JdbcDomain {
    private final TrackerBotClient trackerBotClient;
    private final JdbcChatRepository chatRepository;

    public JdbcGithubDomain(
        TrackerBotClient trackerBotClient,
        GithubClient githubClient,
        JdbcChatRepository chatRepository
    ) {
        super(githubClient);
        this.trackerBotClient = trackerBotClient;
        this.chatRepository = chatRepository;
    }

    @Override
    public void update(JdbcLink link) {
        URL parsed = CommonUtils.toURL(link.getUrl());
        githubClient.fetchRepository(toGithubRepository(parsed))
            .ifPresent((githubResponse -> {
                if (githubResponse.lastActivityDate().isAfter(link.getLastCheckTime())) {
                    trackerBotClient.sendUpdate(
                        link,
                        createDescription(githubResponse),
                        chatRepository.findAllByLink(link.getId()).stream().map(Chat::getId).toList()
                    );
                }
            }));
    }
}
