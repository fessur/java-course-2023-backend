package edu.java.bot.configuration;

import edu.java.bot.configuration.props.KafkaConsumerProperties;
import edu.java.bot.gateway.dto.LinkUpdate;
import edu.java.bot.gateway.kafka.LinkUpdatesDLQGateway;
import edu.java.bot.gateway.kafka.LinkUpdatesListener;
import edu.java.bot.service.BotService;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConsumerConfiguration {

    @Bean
    public MessageHandlerMethodFactory kafkaHandlerMethodFactory(LocalValidatorFactoryBean validatorFactory) {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setValidator(validatorFactory);
        return factory;
    }

    @Bean
    public DefaultErrorHandler errorHandler(
        LinkUpdatesDLQGateway linkUpdatesDLQGateway,
        KafkaConsumerProperties kafkaConsumerProperties
    ) {
        BackOff fixedBackOff = new FixedBackOff(
            kafkaConsumerProperties.backoff().interval().toMillis(),
            kafkaConsumerProperties.backoff().maxAttempts()
        );
        DefaultErrorHandler errorHandler = new DefaultErrorHandler((consumerRecord, ex) -> {
            log.error(
                "Couldn't process message, sending to DLQ: {}, exception: {}",
                consumerRecord.value().toString(),
                ex.toString()
            );

            if (consumerRecord.value() instanceof LinkUpdate failedUpdate) {
                linkUpdatesDLQGateway.sendUpdate(failedUpdate);
            }
        }, fixedBackOff);

        errorHandler.addNotRetryableExceptions(
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class
        );

        return errorHandler;
    }

    @Bean
    public ConsumerFactory<String, LinkUpdate> linkUpdatesConsumerFactory(
        KafkaConsumerProperties kafkaConsumerProperties
    ) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerProperties.bootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerProperties.groupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConsumerProperties.autoOffsetReset());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaConsumerProperties.maxPollIntervalMs());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaConsumerProperties.enableAutoCommit());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, LinkUpdate.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LinkUpdate> linkUpdatesContainerFactory(
        ConsumerFactory<String, LinkUpdate> consumerFactory,
        KafkaConsumerProperties kafkaConsumerProperties,
        DefaultErrorHandler errorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, LinkUpdate> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(kafkaConsumerProperties.concurrency());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public LinkUpdatesListener linkUpdatesListener(BotService botService) {
        return new LinkUpdatesListener(botService);
    }
}
