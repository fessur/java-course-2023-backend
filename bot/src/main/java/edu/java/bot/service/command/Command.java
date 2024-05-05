package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.exception.TooManyRequestsException;
import org.springframework.web.reactive.function.client.WebClientException;

public abstract class Command {
    public abstract String command();

    public abstract String description();

    public boolean supports(Update update) {
        return update.message() != null && update.message().text() != null
            && update.message().text().startsWith("/" + command());
    }

    public SendMessage process(Update update) {
        try {
            return processUpdate(update);
        } catch (TooManyRequestsException ex) {
            return new SendMessage(
                update.message().chat().id(),
                "Too many requests.\nPlease, try again later."
            );
        } catch (WebClientException ex) {
            return new SendMessage(
                update.message().chat().id(),
                "Service is temporary unavailable.\nPlease, try again later."
            );
        }
    }


    public BotCommand toApiCommand() {
        return new BotCommand(command(), description());
    }

    @Override
    public String toString() {
        return "/" + command() + " - " + description();
    }

    protected abstract SendMessage processUpdate(Update update);
}
