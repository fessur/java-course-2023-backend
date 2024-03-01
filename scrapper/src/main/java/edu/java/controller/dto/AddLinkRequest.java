package edu.java.controller.dto;

import edu.java.controller.validation.annotation.SupportedLink;

public class AddLinkRequest {
    @SupportedLink
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
