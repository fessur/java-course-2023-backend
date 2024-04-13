package edu.java.service.site.jpa;

import edu.java.client.StackOverflowClient;
import edu.java.gateway.UpdatesGateway;
import edu.java.gateway.dto.LinkUpdate;
import edu.java.service.model.jpa.JpaChat;
import edu.java.service.model.jpa.JpaLink;
import edu.java.service.site.StackOverflow;
import edu.java.util.CommonUtils;
import java.net.URL;

public class JpaStackOverflow extends StackOverflow implements JpaSite {
    private final UpdatesGateway updatesGateway;

    public JpaStackOverflow(StackOverflowClient stackOverflowClient, UpdatesGateway updatesGateway) {
        super(stackOverflowClient);
        this.updatesGateway = updatesGateway;
    }

    @Override
    public void update(JpaLink link) {
        URL parsed = CommonUtils.toURL(link.getUrl());
        stackOverflowClient.fetchPost(toStackOverflowQuestion(parsed))
            .ifPresent(stackOverflowResponse -> {
                if (stackOverflowResponse.lastActivityDate()
                    .isAfter(link.getLastCheckTime())) {
                    updatesGateway.sendUpdate(new LinkUpdate(
                        link.getId(),
                        link.getUrl(),
                        createDescription(stackOverflowResponse),
                        link.getChats().stream().map(JpaChat::getId).toList()
                    ));
                }
            });
    }
}
