package edu.java.bot.client;

import edu.java.bot.client.dto.LinkResponse;
import edu.java.bot.client.dto.ListLinksResponse;

public interface ScrapperClient {
    ListLinksResponse fetchLinks(long chatId);

    void registerChat(long chatId);

    LinkResponse trackLink(long chatId, String link);

    LinkResponse untrackLink(long chatId, String link);
}
