package edu.java.scrapper.kafka;

import edu.java.gateway.KafkaUpdatesGateway;
import edu.java.gateway.dto.LinkUpdate;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class ProducerTest extends KafkaIntegrationTest {
    @Autowired
    private KafkaUpdatesGateway kafkaUpdatesGateway;

    @Test
    public void testSendUpdate() {
        LinkUpdate linkUpdate = new LinkUpdate(
            1,
            "https://github.com/spring-projects/spring-framework",
            "test",
            List.of(1L)
        );

        kafkaUpdatesGateway.sendUpdate(linkUpdate);

        try (KafkaConsumer<String, LinkUpdate> consumer = new KafkaConsumer<>(createProperties())) {
            consumer.subscribe(List.of("updates"));

            ConsumerRecords<String, LinkUpdate> records = consumer.poll(Duration.ofSeconds(10));

            assertThat(records).extracting(ConsumerRecord::value).containsExactly(linkUpdate);
        }
    }

    private Map<String, Object> createProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(JsonDeserializer.VALUE_DEFAULT_TYPE, LinkUpdate.class);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }
}
