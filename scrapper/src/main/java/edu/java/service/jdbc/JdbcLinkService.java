package edu.java.service.jdbc;

import edu.java.repository.jdbc.JdbcChatRepository;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.service.ChatService;
import edu.java.service.DomainService;
import edu.java.service.LinkService;
import edu.java.service.exception.LinkAlreadyTrackingException;
import edu.java.service.exception.NoSuchChatException;
import edu.java.service.exception.NoSuchLinkException;
import edu.java.service.model.jdbc.JdbcLink;
import edu.java.util.CommonUtils;
import java.util.Collection;

public class JdbcLinkService implements LinkService {
    private final JdbcChatRepository chatRepository;
    private final JdbcLinkRepository linkRepository;
    private final DomainService domainService;

    public JdbcLinkService(
        JdbcChatRepository chatRepository,
        JdbcLinkRepository linkRepository,
        DomainService domainService
    ) {
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
        this.domainService = domainService;
    }

    @Override
    public JdbcLink add(String url, long chatId) {
        chatRepository.findById(chatId).orElseThrow(() -> new NoSuchChatException(ChatService.NOT_REGISTERED_MESSAGE));
        String normalized = domainService.normalizeLink(CommonUtils.toURL(url));
        linkRepository.findByURL(normalized).ifPresentOrElse(link -> {
            if (linkRepository.checkConnected(link.getId(), chatId)) {
                throw new LinkAlreadyTrackingException(ALREADY_TRACKING_MESSAGE);
            }
            linkRepository.makeConnected(link.getId(), chatId);
        }, () -> linkRepository.add(new JdbcLink(0, normalized, null, null), chatId));
        return linkRepository.findByURL(normalized).orElseThrow();
    }

    @Override
    public JdbcLink remove(String url, long chatId) {
        chatRepository.findById(chatId).orElseThrow(() -> new NoSuchChatException(ChatService.NOT_REGISTERED_MESSAGE));
        String normalized = domainService.normalizeLink(CommonUtils.toURL(url));
        JdbcLink link =
            linkRepository.findByURL(normalized).orElseThrow(() -> new NoSuchLinkException(NOT_TRACKING_MESSAGE));
        if (!linkRepository.checkConnected(link.getId(), chatId)) {
            throw new NoSuchLinkException(NOT_TRACKING_MESSAGE);
        }
        linkRepository.remove(link.getId(), chatId);
        return link;
    }

    @Override
    public Collection<JdbcLink> listAll(long chatId) {
        chatRepository.findById(chatId).orElseThrow(() -> new NoSuchChatException(ChatService.NOT_REGISTERED_MESSAGE));
        return linkRepository.findAllByChatId(chatId);
    }
}
