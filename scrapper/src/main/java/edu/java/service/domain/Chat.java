package edu.java.service.domain;

import java.util.List;

public record Chat(long id, List<Link> links) {
    public void addLink(Link link) {
        links.add(link);
    }
}
