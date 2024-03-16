package edu.java.repository.jdbc;

import edu.java.repository.LinkRepository;
import edu.java.repository.jdbc.mapper.LinkMapper;
import edu.java.service.domain.Link;
import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcLinkRepository implements LinkRepository {
    private static final LinkMapper LINK_MAPPER = new LinkMapper();
    private final JdbcTemplate jdbcTemplate;

    public JdbcLinkRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @Transactional
    public void add(Link link, long chatId) {
        jdbcTemplate.update("INSERT INTO link (url) VALUES (?)", link.url());
        jdbcTemplate.update(
            "INSERT INTO chat_link (chat_id, link_id) VALUES (?, (SELECT id FROM link WHERE url = ?))",
            chatId,
            link.url()
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
    public Collection<Link> findAllByChatId(long chatId) {
        return jdbcTemplate.query(
            "SELECT * FROM link JOIN chat_link ON link.id = chat_link.link_id WHERE chat_link.chat_id = ?",
            LINK_MAPPER, chatId
        );
    }

    @Override
    public Optional<Link> findByURL(String url) {
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
    public Collection<Link> findAll() {
        return jdbcTemplate.query("SELECT * FROM link", LINK_MAPPER);
    }

    @Override
    public void updateLastCheckTime(long linkId) {
        jdbcTemplate.update("UPDATE link SET last_check_time = CURRENT_TIMESTAMP WHERE id = ?", linkId);
    }

    @Override
    public Collection<Link> findOldest(Duration duration) {
        String query = "SELECT * FROM link WHERE "
            + "last_check_time < CURRENT_TIMESTAMP - INTERVAL '" + duration.getSeconds() + " SECONDS'";
        return jdbcTemplate.query(
            query,
            LINK_MAPPER
        );
    }
}
