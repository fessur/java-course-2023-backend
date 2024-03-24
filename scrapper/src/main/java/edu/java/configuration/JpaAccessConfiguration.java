package edu.java.configuration;

import edu.java.client.GithubClient;
import edu.java.client.StackOverflowClient;
import edu.java.client.TrackerBotClient;
import edu.java.repository.jpa.JpaChatRepository;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.service.ChatService;
import edu.java.service.DomainService;
import edu.java.service.LinkService;
import edu.java.service.LinkUpdaterService;
import edu.java.service.domains.Domain;
import edu.java.service.domains.jpa.JpaDomain;
import edu.java.service.domains.jpa.JpaGithubDomain;
import edu.java.service.domains.jpa.JpaStackOverflowDomain;
import edu.java.service.jpa.JpaChatService;
import edu.java.service.jpa.JpaLinkService;
import edu.java.service.jpa.JpaLinkUpdaterService;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
@EnableJpaRepositories(basePackages = "edu.java.repository.jpa")
public class JpaAccessConfiguration {
    @Bean
    public List<Domain> domains(
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient,
        TrackerBotClient trackerBotClient
    ) {
        return List.of(
            new JpaGithubDomain(githubClient, trackerBotClient),
            new JpaStackOverflowDomain(stackOverflowClient, trackerBotClient)
        );
    }

    @Bean
    public LinkService linkService(
        JpaChatRepository chatRepository,
        JpaLinkRepository linkRepository,
        DomainService domainService
    ) {
        return new JpaLinkService(chatRepository, linkRepository, domainService);
    }

    @Bean
    public ChatService chatService(JpaChatRepository chatRepository) {
        return new JpaChatService(chatRepository);
    }

    @Bean
    public LinkUpdaterService linkUpdaterService(
        JpaLinkRepository linkRepository,
        ApplicationConfig applicationConfig,
        List<JpaDomain> domains
    ) {
        return new JpaLinkUpdaterService(linkRepository, applicationConfig, domains);
    }
}
