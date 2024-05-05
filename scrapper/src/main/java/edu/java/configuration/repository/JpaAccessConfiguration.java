package edu.java.configuration.repository;

import edu.java.client.GithubClient;
import edu.java.client.StackOverflowClient;
import edu.java.configuration.props.ApplicationConfig;
import edu.java.gateway.UpdatesGateway;
import edu.java.repository.jpa.JpaChatRepository;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import edu.java.service.LinkUpdaterService;
import edu.java.service.SiteService;
import edu.java.service.jpa.JpaChatService;
import edu.java.service.jpa.JpaLinkService;
import edu.java.service.jpa.JpaLinkUpdaterService;
import edu.java.service.site.jpa.JpaGithub;
import edu.java.service.site.jpa.JpaSite;
import edu.java.service.site.jpa.JpaStackOverflow;
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
    public List<JpaSite> sites(
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient,
        UpdatesGateway updatesGateway
    ) {
        return List.of(
            new JpaGithub(githubClient, updatesGateway),
            new JpaStackOverflow(stackOverflowClient, updatesGateway)
        );
    }


    @Bean
    public LinkService linkService(
        JpaChatRepository chatRepository,
        JpaLinkRepository linkRepository,
        SiteService siteService
    ) {
        return new JpaLinkService(chatRepository, linkRepository, siteService);
    }

    @Bean
    public ChatService chatService(JpaChatRepository chatRepository) {
        return new JpaChatService(chatRepository);
    }

    @Bean
    public LinkUpdaterService linkUpdaterService(
        JpaLinkRepository linkRepository,
        ApplicationConfig applicationConfig,
        List<JpaSite> sites
    ) {
        return new JpaLinkUpdaterService(linkRepository, applicationConfig, sites);
    }
}
