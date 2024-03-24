package edu.java.client;

import edu.java.service.model.Link;
import java.util.Collection;

public interface TrackerBotClient {
    void sendUpdate(Link link, String description, Collection<Long> chatIds);
}
