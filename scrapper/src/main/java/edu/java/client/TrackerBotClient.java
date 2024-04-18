package edu.java.client;

import edu.java.service.model.Link;
import java.util.List;

public interface TrackerBotClient {
    void sendUpdate(Link link, String description, List<Long> chatIds);
}
