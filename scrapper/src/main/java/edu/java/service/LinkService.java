package edu.java.service;

import edu.java.service.model.Link;
import java.util.Collection;

public interface LinkService {
    Link add(String url, long chatId);

    Link remove(String url, long chatId);

    Collection<? extends Link> listAll(long chatId);
}
