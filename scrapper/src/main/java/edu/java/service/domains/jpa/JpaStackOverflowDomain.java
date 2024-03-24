package edu.java.service.domains.jpa;

import edu.java.client.StackOverflowClient;
import edu.java.client.TrackerBotClient;
import edu.java.service.domains.StackOverflowDomain;
import edu.java.service.model.jpa.JpaChat;
import edu.java.service.model.jpa.JpaLink;
import edu.java.util.CommonUtils;
import java.net.URL;

public class JpaStackOverflowDomain extends StackOverflowDomain implements JpaDomain {
    private final TrackerBotClient trackerBotClient;

    public JpaStackOverflowDomain(StackOverflowClient stackOverflowClient, TrackerBotClient trackerBotClient) {
        super(stackOverflowClient);
        this.trackerBotClient = trackerBotClient;
    }

    @Override
    public void update(JpaLink link) {
        URL parsed = CommonUtils.toURL(link.getUrl());
        stackOverflowClient.fetchPost(toStackOverflowQuestion(parsed))
            .ifPresent(stackOverflowResponse -> {
                if (stackOverflowResponse.lastActivityDate()
                    .isAfter(link.getLastCheckTime())) {
                    trackerBotClient.sendUpdate(
                        link,
                        createDescription(stackOverflowResponse),
                        link.getChats().stream().map(JpaChat::getId).toList()
                    );
                }
            });
    }
}
