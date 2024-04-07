package edu.java.bot.configuration;

import edu.java.bot.configuration.props.KafkaProducerProperties;
import edu.java.bot.gateway.dto.LinkUpdate;
import edu.java.bot.gateway.kafka.LinkUpdatesDLQGateway;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfiguration {
    @Bean
    public ProducerFactory<String, LinkUpdate> producerFactory(KafkaProducerProperties kafkaProducerProperties) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerProperties.bootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaProducerProperties.clientId());
        props.put(ProducerConfig.ACKS_CONFIG, kafkaProducerProperties.acksMode());
        props.put(
            ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,
            (int) kafkaProducerProperties.deliveryTimeout().toMillis()
        );
        props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProducerProperties.lingerMs());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProducerProperties.batchSize());
        props.put(
            ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
            kafkaProducerProperties.maxInFlightPerConnection()
        );
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, kafkaProducerProperties.enableIdempotence());
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, LinkUpdate> botUpdatesDLQProducer(
        ProducerFactory<String, LinkUpdate> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public LinkUpdatesDLQGateway linkUpdatesDLQGateway(KafkaTemplate<String, LinkUpdate> botUpdatesDLQProducer) {
        return new LinkUpdatesDLQGateway(botUpdatesDLQProducer);
    }
}
