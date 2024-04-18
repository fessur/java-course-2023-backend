package edu.java.repository.jdbc;

import edu.java.service.model.jdbc.JdbcLink;
import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

public interface JdbcLinkRepository {
    void add(JdbcLink link, long chatId);

    void remove(long linkId, long chatId);

    Collection<JdbcLink> findAll();

    Collection<JdbcLink> findOldest(Duration duration);

    Collection<JdbcLink> findAllByChatId(long chatId);

    Optional<JdbcLink> findByURL(String url);

    boolean checkConnected(long linkId, long chatId);

    void makeConnected(long linkId, long chatId);

    void updateLastCheckTime(long linkId);
}
