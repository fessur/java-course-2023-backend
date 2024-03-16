package edu.java.service.jdbc;

import edu.java.repository.ChatRepository;
import edu.java.repository.LinkRepository;
import edu.java.service.LinkService;
import edu.java.service.LinkUpdaterService;
import edu.java.service.domain.Link;
import edu.java.service.exception.LinkAlreadyTrackingException;
import edu.java.service.exception.NoSuchChatException;
import edu.java.service.exception.NoSuchLinkException;
import edu.java.util.CommonUtils;
import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public class JdbcLinkService implements LinkService {
    private static final String NOT_REGISTERED_MESSAGE = "Chat is not registered";
    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;
    private final LinkUpdaterService linkUpdaterService;

    public JdbcLinkService(
        ChatRepository chatRepository,
        LinkRepository linkRepository,
        LinkUpdaterService linkUpdaterService
    ) {
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
        this.linkUpdaterService = linkUpdaterService;
    }

    @Override
    public Link add(String url, long chatId) {
        chatRepository.findById(chatId).orElseThrow(() -> new NoSuchChatException(NOT_REGISTERED_MESSAGE));
        String normalized = linkUpdaterService.normalizeLink(CommonUtils.toURL(url));
        linkRepository.findByURL(normalized).ifPresentOrElse(link -> {
            if (linkRepository.checkConnected(link.id(), chatId)) {
                throw new LinkAlreadyTrackingException("Link is already tracking");
            }
            linkRepository.makeConnected(link.id(), chatId);
        }, () -> linkRepository.add(new Link(0, normalized, null, null), chatId));
        return linkRepository.findByURL(normalized).orElseThrow();
    }

    @Override
    public Link remove(String url, long chatId) {
        chatRepository.findById(chatId).orElseThrow(() -> new NoSuchChatException(NOT_REGISTERED_MESSAGE));
        String normalized = linkUpdaterService.normalizeLink(CommonUtils.toURL(url));
        Link link = linkRepository.findByURL(normalized).orElseThrow(() -> new NoSuchLinkException("Cannot find link"));
        if (!linkRepository.checkConnected(link.id(), chatId)) {
            throw new NoSuchLinkException("Link is not tracked by this chat");
        }
        linkRepository.remove(link.id(), chatId);
        return link;
    }

    @Override
    public Collection<Link> listAll(long chatId) {
        chatRepository.findById(chatId).orElseThrow(() -> new NoSuchChatException(NOT_REGISTERED_MESSAGE));
        return linkRepository.findAllByChatId(chatId);
    }
}
