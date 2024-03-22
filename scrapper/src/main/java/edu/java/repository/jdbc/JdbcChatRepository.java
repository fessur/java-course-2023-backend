package edu.java.repository.jdbc;

import edu.java.service.model.jdbc.JdbcChat;
import java.util.Collection;
import java.util.Optional;

public interface JdbcChatRepository {
    void add(JdbcChat chat);

    Optional<JdbcChat> findById(long id);

    void remove(long id);

    Collection<JdbcChat> findAllByLink(long linkId);
}
