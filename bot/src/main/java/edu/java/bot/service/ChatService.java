package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.domain.Chat;
import edu.java.bot.repository.ChatRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public void register(Update update) {
        Chat chat = new Chat(update.message().chat().id(), new ArrayList<>());
        chatRepository.save(chat);
    }

    public Optional<Chat> find(Update update) {
        return chatRepository.findById(update.message().chat().id());
    }
}
