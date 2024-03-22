package edu.java.service.domains;

import java.net.URL;

public interface Domain {
    boolean isValid(URL url);

    boolean exists(URL url);

    String notExistsMessage();

    String normalize(URL url);
}
