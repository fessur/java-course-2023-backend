package edu.java.service.jdbc;

import edu.java.client.GithubClient;
import edu.java.client.StackOverflowClient;
import edu.java.client.TrackerBotClient;
import edu.java.configuration.ApplicationConfig;
import edu.java.repository.ChatRepository;
import edu.java.repository.LinkRepository;
import edu.java.repository.dto.Chat;
import edu.java.repository.dto.Link;
import edu.java.service.LinkUpdaterService;
import edu.java.util.CommonUtils;
import org.springframework.stereotype.Service;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
public class JdbcLinkUpdaterService implements LinkUpdaterService {
    private static final List<String> SUPPORTED_DOMAINS = List.of("github.com", "stackoverflow.com");
    private final StackOverflowClient stackOverflowClient;
    private final GithubClient githubClient;
    private final TrackerBotClient trackerBotClient;
    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;
    private final ApplicationConfig applicationConfig;

    public JdbcLinkUpdaterService(
        StackOverflowClient stackOverflowClient,
        GithubClient githubClient,
        TrackerBotClient trackerBotClient,
        ChatRepository chatRepository,
        LinkRepository linkRepository,
        ApplicationConfig applicationConfig
    ) {
        this.stackOverflowClient = stackOverflowClient;
        this.githubClient = githubClient;
        this.trackerBotClient = trackerBotClient;
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
        this.applicationConfig = applicationConfig;
    }

    @Override
    public int update() {
        linkRepository.findOldest(applicationConfig.scheduler().forceCheckDelay()).forEach(link -> {
            URL parsed = CommonUtils.toURL(link.url());
            switch (CommonUtils.toURL(link.url()).getHost()) {
                case "github.com":
                    githubClient.fetchRepository(LinkUpdaterService.toGithubRepository(parsed))
                        .ifPresent((githubResponse -> {
                            if (githubResponse.lastActivityDate().isAfter(link.lastCheckTime())) {
                                linkUpdatedAction(link);
                            }
                        }));
                    break;
                case "stackoverflow.com":
                    stackOverflowClient.fetchPost(LinkUpdaterService.toStackOverflowQuestion(parsed))
                        .ifPresent(stackOverflowResponse -> {
                            if (stackOverflowResponse.items().getFirst().lastActivityDate()
                                .isAfter(link.lastCheckTime())) {
                                linkUpdatedAction(link);
                            }
                        });
                default:
                    throw new IllegalStateException("Database is not consistent");
            }
        });
        return 0;
    }

    @Override
    public Optional<String> validateLink(URL url) {
        if (isGithubRepository(url)) {
            if (githubClient.exists(LinkUpdaterService.toGithubRepository(url))) {
                return Optional.empty();
            }
            return Optional.of("Cannot find such repository.");
        }
        if (isStackOverflowQuestion(url)) {
            if (stackOverflowClient.exists(LinkUpdaterService.toStackOverflowQuestion(url))) {
                return Optional.empty();
            }
            return Optional.of("Cannot find such question.");
        }
        return Optional.of("Domain " + url.getHost() + " is not supported yet. List of all supported domains:\n"
            + CommonUtils.joinEnumerated(SUPPORTED_DOMAINS, 1));
    }

    private boolean isGithubRepository(URL url) {
        String[] parts = url.getPath().split("/");
        return url.getProtocol().equals("https")
            && url.getHost().equals("github.com")
            && parts.length >= 2
            && !parts[1].isEmpty()
            && !parts[2].isEmpty();
    }

    private boolean isStackOverflowQuestion(URL url) {
        if (!url.getProtocol().equals("https") || !url.getHost().equals("stackoverflow.com")) {
            return false;
        }

        String[] parts = url.getPath().split("/");
        if (parts.length < 2 || !parts[1].equals("questions")) {
            return false;
        }

        try {
            Long.parseLong(parts[2]);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void linkUpdatedAction(Link link) {
        linkRepository.updateLastCheckTime(link.id());
        trackerBotClient.sendUpdate(
            link,
            "",
            chatRepository.findAllByLink(link.id()).stream().map(Chat::id).toList()
        );
    }
}
