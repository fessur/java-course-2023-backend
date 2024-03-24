package edu.java.repository.jdbc.implementation;

import edu.java.repository.jdbc.JdbcChatRepository;
import edu.java.repository.jdbc.mapper.ChatMapper;
import edu.java.service.model.jdbc.JdbcChat;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

public class JdbcChatRepositoryImpl implements JdbcChatRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final ChatMapper CHAT_MAPPER = new ChatMapper();

    public JdbcChatRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void add(JdbcChat chat) {
        jdbcTemplate.update("INSERT INTO chat (id) VALUES (:id)", Map.of("id", chat.getId()));
    }

    @Override
    public Optional<JdbcChat> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM chat WHERE id = :id", Map.of("id", id), CHAT_MAPPER)
            .stream().findFirst();
    }

    @Override
    @Transactional
    public void remove(long id) {
        jdbcTemplate.update("DELETE FROM chat_link WHERE chat_id = :id", Map.of("id", id));
        jdbcTemplate.update("DELETE FROM chat WHERE id = :id", Map.of("id", id));
        jdbcTemplate.update("DELETE FROM link WHERE NOT EXISTS"
            + "(SELECT 1 FROM chat_link WHERE link.id = chat_link.link_id)", Map.of());
    }

    @Override
    public Collection<JdbcChat> findAllByLink(long linkId) {
        return jdbcTemplate.query(
            "SELECT c.* FROM chat c JOIN chat_link ON c.id = chat_link.chat_id where chat_link.link_id = :linkId",
            Map.of("linkId", linkId), CHAT_MAPPER
        );
    }
}
