package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
public class ListCommand extends Command {
    @Override
    public String command() {
        return "list";
    }

    @Override
    public String description() {
        return "Not implemented yet";
    }

    @Override
    public SendMessage process(Update update) {
        return new SendMessage(update.message().chat().id(), "Not implemented yet");
    }
}
