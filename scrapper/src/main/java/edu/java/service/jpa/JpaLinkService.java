package edu.java.service.jpa;

import edu.java.repository.jpa.JpaChatRepository;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.service.ChatService;
import edu.java.service.DomainService;
import edu.java.service.LinkService;
import edu.java.service.exception.LinkAlreadyTrackingException;
import edu.java.service.exception.NoSuchChatException;
import edu.java.service.exception.NoSuchLinkException;
import edu.java.service.model.jpa.JpaChat;
import edu.java.service.model.jpa.JpaLink;
import edu.java.util.CommonUtils;
import java.util.Collection;
import org.springframework.transaction.annotation.Transactional;

public class JpaLinkService implements LinkService {
    private final JpaChatRepository chatRepository;
    private final JpaLinkRepository linkRepository;
    private final DomainService domainService;

    public JpaLinkService(
        JpaChatRepository chatRepository,
        JpaLinkRepository linkRepository,
        DomainService domainService
    ) {
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
        this.domainService = domainService;
    }

    @Override
    @Transactional
    public JpaLink add(String url, long chatId) {
        String normalized = domainService.normalizeLink(CommonUtils.toURL(url));
        JpaChat chat =
            chatRepository.findById(chatId)
                .orElseThrow(() -> new NoSuchChatException(ChatService.NOT_REGISTERED_MESSAGE));
        chat.getLinks().stream().filter(l -> l.getUrl().equals(normalized)).findFirst().ifPresent(l -> {
            throw new LinkAlreadyTrackingException(ALREADY_TRACKING_MESSAGE);
        });
        JpaLink link = linkRepository.findByUrl(url).orElseGet(() -> {
            JpaLink newLink = new JpaLink();
            newLink.setUrl(normalized);
            return newLink;
        });
        link.getChats().add(chat);
        chat.getLinks().add(link);
        return linkRepository.save(link);
    }

    @Override
    @Transactional
    public JpaLink remove(String url, long chatId) {
        String normalized = domainService.normalizeLink(CommonUtils.toURL(url));
        JpaChat chat = chatRepository.findById(chatId).orElseThrow(() ->
            new NoSuchChatException(ChatService.NOT_REGISTERED_MESSAGE)
        );
        JpaLink link = chat.getLinks().stream().filter(l -> l.getUrl().equals(normalized)).findFirst()
            .orElseThrow(() -> new NoSuchLinkException(NOT_TRACKING_MESSAGE));
        chat.getLinks().remove(link);
        link.getChats().remove(chat);
        return link;
    }

    @Override
    public Collection<JpaLink> listAll(long chatId) {
        return chatRepository.findById(chatId)
            .orElseThrow(() -> new NoSuchChatException(ChatService.NOT_REGISTERED_MESSAGE))
            .getLinks();
    }
}
