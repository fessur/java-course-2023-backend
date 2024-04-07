package edu.java.bot.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

@Configuration
@EnableKafka
public class ListenerConfig implements KafkaListenerConfigurer {
    private final MessageHandlerMethodFactory kafkaHandlerMethodFactory;

    public ListenerConfig(MessageHandlerMethodFactory kafkaHandlerMethodFactory) {
        this.kafkaHandlerMethodFactory = kafkaHandlerMethodFactory;
    }

    @Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(kafkaHandlerMethodFactory);
    }
}
