package edu.java.bot.kafka;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class KafkaIntegrationTest {
    public static KafkaContainer KAFKA;

    static {
        KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.4"));
        KAFKA.start();
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("kafka.producer.bootstrapServers", KAFKA::getBootstrapServers);
        registry.add("kafka.consumer.bootstrapServers", KAFKA::getBootstrapServers);
        registry.add("app.telegram-token", () -> "testToken");
    }
}
