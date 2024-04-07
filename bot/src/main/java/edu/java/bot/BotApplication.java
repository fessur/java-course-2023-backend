package edu.java.bot;

import edu.java.bot.configuration.props.ApplicationConfig;
import edu.java.bot.configuration.props.KafkaConsumerProperties;
import edu.java.bot.configuration.props.KafkaProducerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationConfig.class, KafkaConsumerProperties.class, KafkaProducerProperties.class})
public class BotApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }
}
