package edu.java.service;

import edu.java.repository.ChatRepository;
import edu.java.service.domain.Chat;
import edu.java.service.exception.ChatAlreadyRegisteredException;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public void register(long id) {
        chatRepository.findById(id).ifPresent(c -> {
            throw new ChatAlreadyRegisteredException("Chat is already registered");
        });
        Chat chat = new Chat(id, new ArrayList<>());
        chatRepository.save(chat);
    }

    public void delete(long id) {
        chatRepository.delete(id);
    }
}
