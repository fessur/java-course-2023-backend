package edu.java.service.site.jpa;

import edu.java.client.StackOverflowClient;
import edu.java.client.TrackerBotClient;
import edu.java.service.model.jpa.JpaChat;
import edu.java.service.model.jpa.JpaLink;
import edu.java.service.site.StackOverflow;
import edu.java.util.CommonUtils;
import java.net.URL;

public class JpaStackOverflow extends StackOverflow implements JpaSite {
    private final TrackerBotClient trackerBotClient;

    public JpaStackOverflow(StackOverflowClient stackOverflowClient, TrackerBotClient trackerBotClient) {
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
                        "Question " + stackOverflowResponse.title(),
                        link.getChats().stream().map(JpaChat::getId).toList()
                    );
                }
            });
    }
}
