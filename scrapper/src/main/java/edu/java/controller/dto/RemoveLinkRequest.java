package edu.java.controller.dto;

import edu.java.controller.validation.annotation.SupportedLink;

public record RemoveLinkRequest(@SupportedLink String link) {
}
