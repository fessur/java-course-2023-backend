package edu.java.service.site;

import java.net.URL;

public interface Site {
    boolean isValid(URL url);

    boolean exists(URL url);

    String notExistsMessage();

    String normalize(URL url);
}
