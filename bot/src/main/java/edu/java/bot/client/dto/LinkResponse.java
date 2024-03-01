package edu.java.bot.client.dto;

import java.net.URL;

public class LinkResponse {
    private long id;
    private URL url;

    public long getId() {
        return id;
    }

    public URL getUrl() {
        return url;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
