package edu.java.bot.client.dto;

import java.util.List;

public class ListLinksResponse {
    private List<LinkResponse> links;
    private long size;

    public List<LinkResponse> getLinks() {
        return links;
    }

    public void setLinks(List<LinkResponse> links) {
        this.links = links;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
