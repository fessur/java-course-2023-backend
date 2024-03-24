package edu.java.service.jdbc;

import edu.java.repository.jdbc.JdbcChatRepository;
import edu.java.service.ChatService;
import edu.java.service.exception.ChatAlreadyRegisteredException;
import edu.java.service.exception.NoSuchChatException;
import edu.java.service.model.jdbc.JdbcChat;
import org.springframework.stereotype.Service;

@Service
public class JdbcChatService implements ChatService {
    private final JdbcChatRepository chatRepository;

    public JdbcChatService(JdbcChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    public void register(long chatId) {
        chatRepository.findById(chatId).ifPresentOrElse(c -> {
            throw new ChatAlreadyRegisteredException("Chat is already registered");
        }, () -> {
            JdbcChat chat = new JdbcChat(chatId, null);
            chatRepository.add(chat);
        });
    }

    @Override
    public void unregister(long chatId) {
        chatRepository.findById(chatId).ifPresentOrElse(c -> chatRepository.remove(chatId), () -> {
            throw new NoSuchChatException("Chat is not registered");
        });
    }
}
