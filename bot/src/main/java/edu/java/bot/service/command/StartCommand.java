package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
public class StartCommand extends Command {
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
        return new SendMessage(update.message().chat().id(), "Hello! Welcome to our bot!");
    }
}
