package edu.java.service.jpa;

import edu.java.repository.jpa.JpaChatRepository;
import edu.java.service.ChatService;
import edu.java.service.exception.ChatAlreadyRegisteredException;
import edu.java.service.exception.NoSuchChatException;
import edu.java.service.model.jpa.JpaChat;
import org.springframework.transaction.annotation.Transactional;

public class JpaChatService implements ChatService {
    private final JpaChatRepository chatRepository;

    public JpaChatService(JpaChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    @Transactional
    public void register(long chatId) {
        chatRepository.findById(chatId).ifPresentOrElse(c -> {
            throw new ChatAlreadyRegisteredException(ALREADY_REGISTERED_MESSAGE);
        }, () -> {
            JpaChat chat = new JpaChat();
            chat.setId(chatId);
            chatRepository.save(chat);
        });
    }

    @Override
    @Transactional
    public void unregister(long chatId) {
        chatRepository.findById(chatId).ifPresentOrElse(c -> {
            c.getLinks().forEach(l -> l.getChats().remove(c));
            chatRepository.deleteById(chatId);
        }, () -> {
            throw new NoSuchChatException(NOT_REGISTERED_MESSAGE);
        });
    }
}
