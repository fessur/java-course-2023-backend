package edu.java.service.site.jdbc;

import edu.java.client.StackOverflowClient;
import edu.java.gateway.UpdatesGateway;
import edu.java.gateway.dto.LinkUpdate;
import edu.java.repository.jdbc.JdbcChatRepository;
import edu.java.service.model.Chat;
import edu.java.service.model.jdbc.JdbcLink;
import edu.java.service.site.StackOverflow;
import edu.java.util.CommonUtils;
import java.net.URL;

public class JdbcStackOverflow extends StackOverflow implements JdbcSite {
    private final UpdatesGateway updatesGateway;
    private final JdbcChatRepository chatRepository;

    public JdbcStackOverflow(
        UpdatesGateway updatesGateway,
        StackOverflowClient stackOverflowClient,
        JdbcChatRepository chatRepository
    ) {
        super(stackOverflowClient);
        this.updatesGateway = updatesGateway;
        this.chatRepository = chatRepository;
    }

    @Override
    public void update(JdbcLink link) {
        URL parsed = CommonUtils.toURL(link.getUrl());
        stackOverflowClient.fetchPost(toStackOverflowQuestion(parsed))
            .ifPresent(stackOverflowResponse -> {
                if (stackOverflowResponse.lastActivityDate()
                    .isAfter(link.getLastCheckTime())) {
                    updatesGateway.sendUpdate(new LinkUpdate(
                        link.getId(),
                        link.getUrl(),
                        createDescription(stackOverflowResponse),
                        chatRepository.findAllByLink(link.getId()).stream().map(Chat::getId).toList()
                    ));
                }
            });
    }
}
