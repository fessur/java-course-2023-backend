package edu.java.bot.gateway.kafka;

import edu.java.bot.gateway.dto.LinkUpdate;
import edu.java.bot.service.BotService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;

@Slf4j
public class LinkUpdatesListener {
    private final BotService botService;

    public LinkUpdatesListener(BotService botService) {
        this.botService = botService;
    }

    @KafkaListener(topics = "updates", containerFactory = "linkUpdatesContainerFactory")
    public void handleMessage(@Payload @Valid LinkUpdate linkUpdate, Acknowledgment acknowledgment) {
        log.info(
            "Link update received: [{}, {}, {}, {}]",
            linkUpdate.id(),
            linkUpdate.description(),
            linkUpdate.url(),
            linkUpdate.tgChatIds()
        );

        botService.sendMessages(
            linkUpdate.tgChatIds(),
            linkUpdate.url(),
            linkUpdate.description()
        );
        acknowledgment.acknowledge();
    }
}
