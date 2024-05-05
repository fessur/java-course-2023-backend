package edu.java.gateway;

import edu.java.gateway.dto.LinkUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
public class KafkaUpdatesGateway implements UpdatesGateway {
    private static final String ERROR_LOG_MSG = "Cannot send link update: {}";
    private final KafkaTemplate<String, LinkUpdate> scrapperUpdatesProducer;

    public KafkaUpdatesGateway(KafkaTemplate<String, LinkUpdate> scrapperUpdatesProducer) {
        this.scrapperUpdatesProducer = scrapperUpdatesProducer;
    }

    @Override
    public void sendUpdate(LinkUpdate linkUpdate) {
        try {
            scrapperUpdatesProducer.send("updates", linkUpdate)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error(ERROR_LOG_MSG, linkUpdate, throwable);
                    } else {
                        log.info(
                            "Sent link update: {}, topic: {}, offset: {}",
                            linkUpdate,
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().offset()
                        );
                    }
                });
        } catch (Exception ex) {
            log.error(ERROR_LOG_MSG, linkUpdate, ex);
        }
    }
}
