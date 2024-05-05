package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.client.dto.LinkResponse;
import edu.java.bot.client.exception.BadRequestException;
import edu.java.bot.client.exception.NotFoundException;
import edu.java.bot.util.CommonUtils;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand extends Command {
    private final ScrapperClient scrapperClient;

    public UntrackCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

    @Override
    public String command() {
        return "untrack";
    }

    @Override
    public String description() {
        return "Stop tracking a link";
    }

    @Override
    protected SendMessage processUpdate(Update update) {
        try {
            LinkResponse linkResponse = scrapperClient.untrackLink(
                update.message().chat().id(),
                CommonUtils.cutFirstWord(update.message().text())
            );
            return new SendMessage(update.message().chat().id(),
                "Link " + linkResponse.url() + " is no longer being tracked.");
        } catch (BadRequestException ex) {
            return new SendMessage(update.message().chat().id(), "The link is not correct");
        } catch (NotFoundException ex) {
            return new SendMessage(update.message().chat().id(), "You are not tracking this link yet");
        }
    }
}
