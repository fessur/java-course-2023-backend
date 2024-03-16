package edu.java.service;

import edu.java.repository.dto.Link;
import java.net.URI;
import java.util.Collection;
import java.util.List;

public interface LinkService {
    Link add(String url, long chatId);
    Link remove(String url, long chatId);
    Collection<Link> listAll(long chatId);
    List<String> getSupportedDomains();
    boolean isSupported(String domain);
}
