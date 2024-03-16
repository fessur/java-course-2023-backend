package edu.java.repository.jdbc;

import edu.java.repository.LinkRepository;
import edu.java.repository.dto.Link;
import edu.java.repository.jdbc.mapper.LinkMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.sql.DataSource;
import java.util.Collection;
import java.util.Optional;

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
        jdbcTemplate.update("INSERT INTO chat_link (chat_id, link_id) VALUES (?, (SELECT id FROM link WHERE url = ?))", chatId, link.url());
    }

    @Override
    @Transactional
    public void remove(long linkId, long chatId) {
        jdbcTemplate.update("DELETE FROM chat_link WHERE chat_id = ? AND link_id = ?", chatId, linkId);
        jdbcTemplate.update("DELETE FROM link WHERE id IN" +
            "(SELECT id FROM link\n LEFT JOIN chat_link ON link.id = chat_link.link_id WHERE chat_link.link_id IS NULL)");
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
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject("SELECT EXISTS (SELECT 1 FROM chat_link WHERE chat_id = ? AND link_id = ?)", Boolean.class, chatId, linkId));
    }

    @Override
    public void makeConnected(long linkId, long chatId) {
        jdbcTemplate.update("INSERT INTO chat_link (chat_id, link_id) VALUES (?, ?)", chatId, linkId);
    }
}
