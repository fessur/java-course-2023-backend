package edu.java.service;

import edu.java.repository.dto.Link;
import java.util.Collection;

public interface LinkService {
    Link add(String url, long chatId);
    Link remove(String url, long chatId);
    Collection<Link> listAll(long chatId);
}
