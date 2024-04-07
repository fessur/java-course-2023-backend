package edu.java.gateway;

import edu.java.gateway.dto.LinkUpdate;

public interface UpdatesGateway {
    void sendUpdate(LinkUpdate linkUpdate);
}
