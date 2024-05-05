package edu.java.bot.kafka;

import edu.java.bot.gateway.dto.LinkUpdate;
import edu.java.bot.service.BotService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.ActiveProfiles;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class ListenerTest extends KafkaIntegrationTest {

    @MockBean
    private BotService botService;

    @Test
    public void testListener() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(botService).sendMessages(anyList(), anyString(), anyString());

        LinkUpdate linkUpdate = new LinkUpdate(
            1,
            "https://github.com/spring-projects/spring-framework",
            "test",
            List.of(1L)
        );

        KafkaTemplate<String, LinkUpdate> kafkaTemplate = createProducer();

        kafkaTemplate.send("updates", linkUpdate);

        boolean await = latch.await(10, TimeUnit.SECONDS);
        assertThat(await).isTrue();

        verify(botService, times(1)).sendMessages(
            linkUpdate.tgChatIds(),
            linkUpdate.url(),
            linkUpdate.description()
        );
    }

    @Test
    public void testDLQ() {
        LinkUpdate invalidUpdate = new LinkUpdate(
            1,
            "https://github.com/spring-projects/spring-framework",
            "test",
            null
        );

        KafkaTemplate<String, LinkUpdate> kafkaTemplate = createProducer();

        kafkaTemplate.send("updates", invalidUpdate);

        try (KafkaConsumer<String, LinkUpdate> consumer = new KafkaConsumer<>(createConsumerProperties())) {
            consumer.subscribe(List.of("updates_dlq"));

            ConsumerRecords<String, LinkUpdate> records = consumer.poll(Duration.ofSeconds(10));

            assertThat(records).extracting(ConsumerRecord::value).containsExactly(invalidUpdate);
        }
    }

    private KafkaTemplate<String, LinkUpdate> createProducer() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
    }

    private Map<String, Object> createConsumerProperties() {
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
