package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.client.dto.LinkResponse;
import edu.java.bot.client.dto.ListLinksResponse;
import edu.java.bot.util.CommonUtils;
import org.springframework.stereotype.Component;

@Component
public class ListCommand extends Command {
    private final ScrapperClient scrapperClient;

    public ListCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

    @Override
    public String command() {
        return "list";
    }

    @Override
    public String description() {
        return "Show all tracked links";
    }

    @Override
    public SendMessage process(Update update) {
        ListLinksResponse links = scrapperClient.fetchLinks(update.message().chat().id());
        if (links.getLinks().isEmpty()) {
            return new SendMessage(
                update.message().chat().id(),
                "You don't have any tracked links.\nUse /track to start tracking."
            );
        }
        return new SendMessage(
            update.message().chat().id(),
            "Your tracked links:\n"
                + CommonUtils.joinEnumerated(links.getLinks().stream().map(LinkResponse::getUrl).toList(), 1)
        );
    }
}
