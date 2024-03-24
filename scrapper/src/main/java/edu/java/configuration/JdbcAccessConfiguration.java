package edu.java.configuration;

import edu.java.client.GithubClient;
import edu.java.client.StackOverflowClient;
import edu.java.client.TrackerBotClient;
import edu.java.repository.jdbc.JdbcChatRepository;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.implementation.JdbcChatRepositoryImpl;
import edu.java.repository.jdbc.implementation.JdbcLinkRepositoryImpl;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import edu.java.service.LinkUpdaterService;
import edu.java.service.SiteService;
import edu.java.service.jdbc.JdbcChatService;
import edu.java.service.jdbc.JdbcLinkService;
import edu.java.service.jdbc.JdbcLinkUpdaterService;
import edu.java.service.site.Site;
import edu.java.service.site.jdbc.JdbcGithub;
import edu.java.service.site.jdbc.JdbcSite;
import edu.java.service.site.jdbc.JdbcStackOverflow;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {
    @Bean
    public JdbcChatRepository chatRepository(DataSource dataSource) {
        return new JdbcChatRepositoryImpl(dataSource);
    }

    @Bean
    public JdbcLinkRepository linkRepository(DataSource dataSource) {
        return new JdbcLinkRepositoryImpl(dataSource);
    }

    @Bean
    public List<Site> sites(
        TrackerBotClient trackerBotClient,
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient,
        JdbcChatRepository chatRepository
    ) {
        return List.of(
            new JdbcGithub(trackerBotClient, githubClient, chatRepository),
            new JdbcStackOverflow(trackerBotClient, stackOverflowClient, chatRepository)
        );
    }

    @Bean
    public LinkService linkService(
        JdbcChatRepository chatRepository,
        JdbcLinkRepository linkRepository,
        SiteService siteService
    ) {
        return new JdbcLinkService(chatRepository, linkRepository, siteService);
    }

    @Bean
    public ChatService chatService(JdbcChatRepository chatRepository) {
        return new JdbcChatService(chatRepository);
    }

    @Bean
    public LinkUpdaterService linkUpdaterService(
        JdbcLinkRepository linkRepository,
        ApplicationConfig applicationConfig,
        List<JdbcSite> sites
    ) {
        return new JdbcLinkUpdaterService(linkRepository, applicationConfig, sites);
    }
}
