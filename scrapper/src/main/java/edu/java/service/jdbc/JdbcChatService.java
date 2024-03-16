package edu.java.service.jdbc;

import edu.java.repository.ChatRepository;
import edu.java.service.ChatService;
import edu.java.service.domain.Chat;
import edu.java.service.exception.ChatAlreadyRegisteredException;
import edu.java.service.exception.NoSuchChatException;
import org.springframework.stereotype.Service;

@Service
public class JdbcChatService implements ChatService {
    private final ChatRepository chatRepository;

    public JdbcChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    public void register(long chatId) {
        chatRepository.findById(chatId).ifPresentOrElse(c -> {
            throw new ChatAlreadyRegisteredException("Chat is already registered");
        }, () -> {
            Chat chat = new Chat(chatId, null);
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
