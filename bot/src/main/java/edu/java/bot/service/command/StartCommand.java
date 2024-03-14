package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.client.exception.ConflictException;
import org.springframework.stereotype.Component;

@Component
public class StartCommand extends Command {
    private final ScrapperClient scrapperClient;

    public StartCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

    @Override
    public String command() {
        return "start";
    }

    @Override
    public String description() {
        return "Start working with the bot";
    }

    @Override
    public SendMessage process(Update update) {
        try {
            scrapperClient.registerChat(update.message().chat().id());
        } catch (ConflictException ex) {
            return new SendMessage(
                update.message().chat().id(),
                "You are already working with our bot. Use /help to see a list of all possible commands");
        }
        return new SendMessage(update.message().chat().id(), "Hello! Welcome to our bot!");
    }
}
