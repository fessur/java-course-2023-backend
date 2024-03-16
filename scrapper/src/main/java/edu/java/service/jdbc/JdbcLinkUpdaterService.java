package edu.java.service.jdbc;

import edu.java.client.GithubClient;
import edu.java.client.StackOverflowClient;
import edu.java.client.TrackerBotClient;
import edu.java.client.dto.GithubRepositoryRequest;
import edu.java.configuration.ApplicationConfig;
import edu.java.repository.ChatRepository;
import edu.java.repository.LinkRepository;
import edu.java.service.LinkUpdaterService;
import edu.java.service.domain.Chat;
import edu.java.service.domain.Link;
import edu.java.util.CommonUtils;
import jakarta.annotation.PostConstruct;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;

@Service
public class JdbcLinkUpdaterService implements LinkUpdaterService {
    private static final String SUPPORTED_PROTOCOL = "https";
    private static final String GITHUB_DOMAIN = "github.com";
    private static final String STACKOVERFLOW_DOMAIN = "stackoverflow.com";
    private final StackOverflowClient stackOverflowClient;
    private final GithubClient githubClient;
    private final TrackerBotClient trackerBotClient;
    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;
    private final ApplicationConfig applicationConfig;
    private Map<String, DomainInfo> domains;

    @PostConstruct
    public void init() {
        domains = Map.of(
            GITHUB_DOMAIN, new DomainInfo(
                url -> {
                    String[] parts = url.getPath().split("/");
                    return url.getProtocol().equals(SUPPORTED_PROTOCOL)
                        && url.getHost().equals(GITHUB_DOMAIN)
                        && parts.length >= 2
                        && !parts[1].isEmpty()
                        && !parts[2].isEmpty();
                },
                url -> githubClient.exists(toGithubRepository(url)),
                "Cannot find such repository.",
                link -> {
                    URL parsed = CommonUtils.toURL(link.url());
                    githubClient.fetchRepository(toGithubRepository(parsed))
                        .ifPresent((githubResponse -> {
                            if (githubResponse.lastActivityDate().isAfter(link.lastCheckTime())) {
                                trackerBotClient.sendUpdate(
                                    link,
                                    "Repository " + githubResponse.name(),
                                    chatRepository.findAllByLink(link.id()).stream().map(Chat::id).toList()
                                );
                            }
                        }));
                },
                url -> {
                    GithubRepositoryRequest request = toGithubRepository(url);
                    return "https://github.com/" + request.owner() + "/" + request.repo();
                }
            ),
            STACKOVERFLOW_DOMAIN, new DomainInfo(
                url -> {
                    if (!url.getProtocol().equals(SUPPORTED_PROTOCOL) || !url.getHost().equals(STACKOVERFLOW_DOMAIN)) {
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
                },
                url -> stackOverflowClient.exists(toStackOverflowQuestion(url)),
                "Cannot find such question.",
                link -> {
                    URL parsed = CommonUtils.toURL(link.url());
                    stackOverflowClient.fetchPost(toStackOverflowQuestion(parsed))
                        .ifPresent(stackOverflowResponse -> {
                            if (stackOverflowResponse.lastActivityDate()
                                .isAfter(link.lastCheckTime())) {
                                trackerBotClient.sendUpdate(
                                    link,
                                    "Question " + stackOverflowResponse.title(),
                                    chatRepository.findAllByLink(link.id()).stream().map(Chat::id).toList()
                                );
                            }
                        });
                },
                url -> "https://stackoverflow.com/questions/" + toStackOverflowQuestion(url)
            )
        );
    }

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
        Collection<Link> oldest = linkRepository.findOldest(applicationConfig.scheduler().forceCheckDelay());
        oldest.forEach(link -> {
            linkRepository.updateLastCheckTime(link.id());
            String host = CommonUtils.toURL(link.url()).getHost();
            domains.forEach((key, value) -> {
                if (key.equals(host)) {
                    value.updateAction.accept(link);
                }
            });
        });
        return oldest.size();
    }

    @Override
    public Optional<String> validateLink(URL url) {
        for (DomainInfo domainInfo : domains.values()) {
            if (domainInfo.isValid.test(url)) {
                if (domainInfo.exists.test(url)) {
                    return Optional.empty();
                }
                return Optional.of(domainInfo.notExistMessage);
            }
        }
        return Optional.of("Domain " + url.getHost() + " is not supported yet. List of all supported domains:\n"
            + CommonUtils.joinEnumerated(domains.keySet().stream().toList(), 1));
    }

    @Override
    public String normalizeLink(URL url) {
        for (Map.Entry<String, DomainInfo> entry : domains.entrySet()) {
            if (url.getHost().equals(entry.getKey())) {
                return entry.getValue().normalize.apply(url);
            }
        }
        throw new IllegalArgumentException("The domain is not supported");
    }

    private GithubRepositoryRequest toGithubRepository(URL url) {
        String[] parts = url.getPath().split("/");
        return new GithubRepositoryRequest(parts[1], parts[2]);
    }

    private long toStackOverflowQuestion(URL url) {
        return Long.parseLong(url.getPath().split("/")[2]);
    }

    private record DomainInfo(Predicate<URL> isValid,
                              Predicate<URL> exists,
                              String notExistMessage,
                              Consumer<Link> updateAction,
                              Function<URL, String> normalize) {
    }
}
