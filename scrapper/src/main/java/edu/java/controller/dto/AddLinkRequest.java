package edu.java.controller.dto;

import edu.java.controller.validation.annotation.SupportedLink;
import lombok.Getter;
import lombok.Setter;
import java.net.URL;

public record AddLinkRequest(@SupportedLink String link) {
}
