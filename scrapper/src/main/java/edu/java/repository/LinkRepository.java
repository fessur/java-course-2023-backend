package edu.java.repository;

import edu.java.service.domain.Link;
import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

public interface LinkRepository {
    void add(Link link, long chatId);

    void remove(long linkId, long chatId);

    Collection<Link> findAll();

    Collection<Link> findOldest(Duration duration);

    Collection<Link> findAllByChatId(long chatId);

    Optional<Link> findByURL(String url);

    boolean checkConnected(long linkId, long chatId);

    void makeConnected(long linkId, long chatId);

    void updateLastCheckTime(long linkId);
}
