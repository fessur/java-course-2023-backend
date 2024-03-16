package edu.java.controller.dto;

import edu.java.controller.validation.annotation.SupportedLink;
import lombok.Getter;
import lombok.Setter;
import java.net.URL;

@Getter
@Setter
public class AddLinkRequest {
    @SupportedLink
    private String link;
}
