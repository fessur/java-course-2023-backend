package edu.java.service.jdbc;

import edu.java.client.GithubClient;
import edu.java.client.StackOverflowClient;
import edu.java.client.TrackerBotClient;
import edu.java.client.dto.GithubRepositoryResponse;
import edu.java.client.dto.StackOverflowPostResponse;
import edu.java.repository.ChatRepository;
import edu.java.repository.LinkRepository;
import edu.java.repository.dto.Chat;
import edu.java.service.LinkService;
import edu.java.service.LinkUpdaterService;
import edu.java.util.CommonUtils;
import org.springframework.stereotype.Service;
import java.net.URL;

@Service
public class JdbcLinkUpdaterService implements LinkUpdaterService {
    private final StackOverflowClient stackOverflowClient;
    private final GithubClient githubClient;
    private final TrackerBotClient trackerBotClient;
    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;

    public JdbcLinkUpdaterService(
        StackOverflowClient stackOverflowClient,
        GithubClient githubClient,
        TrackerBotClient trackerBotClient,
        ChatRepository chatRepository,
        LinkRepository linkRepository
    ) {
        this.stackOverflowClient = stackOverflowClient;
        this.githubClient = githubClient;
        this.trackerBotClient = trackerBotClient;
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
    }

    @Override
    public int update() {
        linkRepository.findAll().forEach(link -> {
            URL parsed = CommonUtils.toURL(link.url());
            switch (CommonUtils.toURL(link.url()).getHost()) {
                case "github.com":
                    GithubRepositoryResponse githubResponse =
                        githubClient.fetchRepository(LinkService.toGithubRepository(parsed));
                    if (githubResponse.lastActivityDate().isAfter(link.lastCheckTime())) {
                        trackerBotClient.sendUpdate(
                            link,
                            "Github repository has new update!",
                            chatRepository.findAllByLink(link.id()).stream().map(Chat::id).toList()
                        );
                    }
                    break;
                case "stackoverflow.com":
                    StackOverflowPostResponse stackOverflowResponse =
                        stackOverflowClient.fetchPost(LinkService.toStackOverflowQuestion(parsed));
                    if (stackOverflowResponse.items().getFirst().lastActivityDate().isAfter(link.lastCheckTime())) {
                        trackerBotClient.sendUpdate(
                            link,
                            "StackOverflow question has new update!",
                            chatRepository.findAllByLink(link.id()).stream().map(Chat::id).toList()
                        );
                    }
                default:
                    throw new IllegalStateException("Database is not consistent");
            }
        });
        return 0;
    }
}
