package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
public class TrackCommand extends Command {
    @Override
    public String command() {
        return "track";
    }

    @Override
    public String description() {
        return "Start tracking a link";
    }

    @Override
    public SendMessage process(Update update) {
        return new SendMessage(update.message().chat().id(), "Not implemented yet");
    }
}
