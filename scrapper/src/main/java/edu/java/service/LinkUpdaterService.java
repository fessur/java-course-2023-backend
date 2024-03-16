package edu.java.service;

import java.net.URL;
import java.util.Optional;

public interface LinkUpdaterService {
    int update();

    Optional<String> validateLink(URL url);

    String normalizeLink(URL url);
}
