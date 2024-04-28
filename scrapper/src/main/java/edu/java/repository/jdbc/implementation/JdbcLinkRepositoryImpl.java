package edu.java.repository.jdbc.implementation;

import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.mapper.LinkMapper;
import edu.java.service.model.jdbc.JdbcLink;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

public class JdbcLinkRepositoryImpl implements JdbcLinkRepository {
    private static final String CHAT_ID_PARAM = "chatId";
    private static final String LINK_ID_PARAM = "linkId";
    private static final String URL_PARAM = "url";
    private static final LinkMapper LINK_MAPPER = new LinkMapper();
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcLinkRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    @Transactional
    public void add(JdbcLink link, long chatId) {
        jdbcTemplate.update("INSERT INTO link (url) VALUES (:url)", Map.of(URL_PARAM, link.getUrl()));
        jdbcTemplate.update(
            "INSERT INTO chat_link (chat_id, link_id) VALUES (:chatId, (SELECT id FROM link WHERE url = :url))",
            Map.of(
                CHAT_ID_PARAM, chatId,
                URL_PARAM, link.getUrl()
            )
        );
    }

    @Override
    @Transactional
    public void remove(long linkId, long chatId) {
        jdbcTemplate.update(
            "DELETE FROM chat_link WHERE chat_id = :chatId AND link_id = :linkId",
            Map.of(
                CHAT_ID_PARAM, chatId,
                LINK_ID_PARAM, linkId
            )
        );
        jdbcTemplate.update("DELETE FROM link WHERE NOT EXISTS"
            + "(SELECT 1 FROM chat_link WHERE link.id = chat_link.link_id)", Map.of());
    }

    @Override
    public Collection<JdbcLink> findAllByChatId(long chatId) {
        return jdbcTemplate.query(
            "SELECT * FROM link JOIN chat_link ON link.id = chat_link.link_id WHERE chat_link.chat_id = :chatId",
            Map.of(CHAT_ID_PARAM, chatId), LINK_MAPPER
        );
    }

    @Override
    public Optional<JdbcLink> findByURL(String url) {
        return jdbcTemplate.query("SELECT * FROM link WHERE url = :url", Map.of(URL_PARAM, url), LINK_MAPPER)
            .stream().findFirst();
    }

    @Override
    public boolean checkConnected(long linkId, long chatId) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
            "SELECT EXISTS (SELECT 1 FROM chat_link WHERE chat_id = :chatId AND link_id = :linkId)",
            Map.of(
                CHAT_ID_PARAM, chatId,
                LINK_ID_PARAM, linkId
            ),
            Boolean.class
        ));
    }

    @Override
    public void makeConnected(long linkId, long chatId) {
        jdbcTemplate.update(
            "INSERT INTO chat_link (chat_id, link_id) VALUES (:chatId, :linkId)",
            Map.of(
                CHAT_ID_PARAM, chatId,
                LINK_ID_PARAM, linkId
            )
        );
    }

    @Override
    public Collection<JdbcLink> findAll() {
        return jdbcTemplate.query("SELECT * FROM link", LINK_MAPPER);
    }

    @Override
    public void updateLastCheckTime(long linkId) {
        jdbcTemplate.update(
            "UPDATE link SET last_check_time = CURRENT_TIMESTAMP WHERE id = :linkId",
            Map.of(LINK_ID_PARAM, linkId)
        );
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
