package edu.java.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Link {
    private long id;
    private String url;
    private String domain;
}
