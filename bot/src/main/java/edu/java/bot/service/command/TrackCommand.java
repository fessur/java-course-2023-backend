package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.client.dto.LinkResponse;
import edu.java.bot.client.exception.BadRequestException;
import edu.java.bot.client.exception.ConflictException;
import edu.java.bot.client.exception.NotFoundException;
import edu.java.bot.util.CommonUtils;
import org.springframework.stereotype.Component;

@Component
public class TrackCommand extends Command {
    private final ScrapperClient scrapperClient;

    public TrackCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

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
        try {
            LinkResponse linkResponse = scrapperClient.trackLink(
                update.message().chat().id(),
                CommonUtils.cutFirstWord(update.message().text())
            );
            return new SendMessage(
                update.message().chat().id(),
                "Link " + linkResponse.url() + " is now being tracked."
            );
        } catch (ConflictException ex) {
            return new SendMessage(update.message().chat().id(), "You are already tracking this link.");
        } catch (NotFoundException | BadRequestException ex) {
            return new SendMessage(update.message().chat().id(), ex.getDescription());
        }
    }
}
