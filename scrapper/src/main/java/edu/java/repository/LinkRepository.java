package edu.java.repository;

import edu.java.repository.dto.Link;
import java.util.Collection;
import java.util.Optional;

public interface LinkRepository {
    void add(Link link, long chatId);
    void remove(long linkId, long chatId);
    Collection<Link> findAllByChatId(long chatId);
    Optional<Link> findByURL(String url);
    boolean checkConnected(long linkId, long chatId);
    void makeConnected(long linkId, long chatId);
}
