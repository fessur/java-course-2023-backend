package edu.java.bot.gateway.kafka;

import edu.java.bot.gateway.dto.LinkUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
public class LinkUpdatesDLQGateway {
    private final KafkaTemplate<String, LinkUpdate> botUpdatesDLQProducer;

    public LinkUpdatesDLQGateway(KafkaTemplate<String, LinkUpdate> botUpdatesDLQProducer) {
        this.botUpdatesDLQProducer = botUpdatesDLQProducer;
    }

    public void sendUpdate(LinkUpdate linkUpdate) {
        botUpdatesDLQProducer.send("updates_dlq", linkUpdate)
            .whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("Cannot send link update to DLQ: {}, exception: {}", linkUpdate, throwable.toString());
                } else {
                    log.info(
                        "Sent link update to DLQ: {}, topic: {}, offset: {}",
                        linkUpdate,
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().offset()
                    );
                }
            });
    }
}
