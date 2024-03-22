package edu.java.service.domains.jdbc;

import edu.java.client.StackOverflowClient;
import edu.java.client.TrackerBotClient;
import edu.java.repository.jdbc.JdbcChatRepository;
import edu.java.service.domains.StackOverflowDomain;
import edu.java.service.model.Chat;
import edu.java.service.model.jdbc.JdbcLink;
import edu.java.util.CommonUtils;
import java.net.URL;
import org.springframework.stereotype.Component;

@Component
public class JdbcStackOverflowDomain extends StackOverflowDomain implements JdbcDomain {
    private final TrackerBotClient trackerBotClient;
    private final JdbcChatRepository chatRepository;

    public JdbcStackOverflowDomain(
        TrackerBotClient trackerBotClient,
        JdbcChatRepository chatRepository,
        StackOverflowClient stackOverflowClient
    ) {
        super(stackOverflowClient);
        this.trackerBotClient = trackerBotClient;
        this.chatRepository = chatRepository;
    }

    @Override
    public void update(JdbcLink link) {
        URL parsed = CommonUtils.toURL(link.getUrl());
        stackOverflowClient.fetchPost(toStackOverflowQuestion(parsed))
            .ifPresent(stackOverflowResponse -> {
                if (stackOverflowResponse.lastActivityDate()
                    .isAfter(link.getLastCheckTime())) {
                    trackerBotClient.sendUpdate(
                        link,
                        "Question " + stackOverflowResponse.title(),
                        chatRepository.findAllByLink(link.getId()).stream().map(Chat::getId).toList()
                    );
                }
            });
    }
}
