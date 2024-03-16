package edu.java.bot.service;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.bot.TrackerBot;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BotService {
    private final TrackerBot bot;

    public BotService(TrackerBot bot) {
        this.bot = bot;
    }

    public void sendMessage(long chatId, String url, String description) {
        bot.execute(new SendMessage(chatId, String.format("%s has new update!\n%s", description, url)));
    }

    public void sendMessages(List<Long> chatIds, String url, String description) {
        for (Long chatId : chatIds) {
            sendMessage(chatId, url, description);
        }
    }
}
