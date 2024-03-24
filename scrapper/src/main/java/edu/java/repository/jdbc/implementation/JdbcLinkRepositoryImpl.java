package edu.java.repository.jdbc.implementation;

import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.mapper.LinkMapper;
import edu.java.service.model.jdbc.JdbcLink;
import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

public class JdbcLinkRepositoryImpl implements JdbcLinkRepository {
    private static final LinkMapper LINK_MAPPER = new LinkMapper();
    private final JdbcTemplate jdbcTemplate;

    public JdbcLinkRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @Transactional
    public void add(JdbcLink link, long chatId) {
        jdbcTemplate.update("INSERT INTO link (url) VALUES (?)", link.getUrl());
        jdbcTemplate.update(
            "INSERT INTO chat_link (chat_id, link_id) VALUES (?, (SELECT id FROM link WHERE url = ?))",
            chatId,
            link.getUrl()
        );
    }

    @Override
    @Transactional
    public void remove(long linkId, long chatId) {
        jdbcTemplate.update("DELETE FROM chat_link WHERE chat_id = ? AND link_id = ?", chatId, linkId);
        jdbcTemplate.update("DELETE FROM link WHERE NOT EXISTS"
            + "(SELECT 1 FROM chat_link WHERE link.id = chat_link.link_id)");
    }

    @Override
    public Collection<JdbcLink> findAllByChatId(long chatId) {
        return jdbcTemplate.query(
            "SELECT * FROM link JOIN chat_link ON link.id = chat_link.link_id WHERE chat_link.chat_id = ?",
            LINK_MAPPER, chatId
        );
    }

    @Override
    public Optional<JdbcLink> findByURL(String url) {
        return jdbcTemplate.query("SELECT * FROM link WHERE url = ?", LINK_MAPPER, url).stream().findFirst();
    }

    @Override
    public boolean checkConnected(long linkId, long chatId) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
            "SELECT EXISTS (SELECT 1 FROM chat_link WHERE chat_id = ? AND link_id = ?)",
            Boolean.class,
            chatId,
            linkId
        ));
    }

    @Override
    public void makeConnected(long linkId, long chatId) {
        jdbcTemplate.update("INSERT INTO chat_link (chat_id, link_id) VALUES (?, ?)", chatId, linkId);
    }

    @Override
    public Collection<JdbcLink> findAll() {
        return jdbcTemplate.query("SELECT * FROM link", LINK_MAPPER);
    }

    @Override
    public void updateLastCheckTime(long linkId) {
        jdbcTemplate.update("UPDATE link SET last_check_time = CURRENT_TIMESTAMP WHERE id = ?", linkId);
    }

    @Override
    public Collection<JdbcLink> findOldest(Duration duration) {
        String query = "SELECT * FROM link WHERE "
            + "last_check_time < CURRENT_TIMESTAMP - INTERVAL '" + duration.getSeconds() + " SECONDS'";
        return jdbcTemplate.query(
            query,
            LINK_MAPPER
        );
    }
}
