package edu.java.scrapper.database.jdbc;

import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.service.model.Link;
import edu.java.service.model.jdbc.JdbcLink;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

public class JdbcLinkRepositoryTest extends JdbcBaseDatabaseTest {
    @Autowired
    private JdbcLinkRepository linkRepository;

    @Test
    @Transactional
    @Rollback
    public void addTest() {
        String url = "https://github.com/golang/go";
        linkRepository.add(new JdbcLink(-1, url, null, null), 1);

        assertThat(jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM link WHERE url = ?",
            Integer.class,
            url
        )).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM chat_link WHERE link_id IN (SELECT id FROM link WHERE url = ?)",
            Integer.class,
            url
        )).isEqualTo(1);
    }

    @Test
    @Transactional
    @Rollback
    public void findByURLTest() {
        assertThat(linkRepository.findByURL(links.getFirst())).isPresent()
            .hasValueSatisfying(link -> assertThat(links.getFirst()).isEqualTo(link.getUrl()));
    }

    @Test
    @Transactional
    @Rollback
    public void removeTest() {
        assertThat(linkRepository.findByURL(links.get(6))).isPresent().hasValueSatisfying(link -> {
            linkRepository.remove(link.getId(), 4);
            assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chat_link WHERE chat_id = 4 AND link_id = ?",
                Integer.class,
                link.getId()
            )).isEqualTo(0);
            assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM link WHERE url = ?",
                Integer.class,
                links.get(6)
            )).isEqualTo(0);
        });
    }

    @Test
    @Transactional
    @Rollback
    public void findAllByChatIdTest() {
        assertThat(linkRepository.findAllByChatId(1)).extracting(Link::getUrl)
            .containsExactlyInAnyOrder(links.get(0), links.get(1), links.get(2));
        assertThat(linkRepository.findAllByChatId(5)).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    public void checkConnectedTest() {
        assertThat(linkRepository.findByURL(links.getFirst())).isPresent()
            .hasValueSatisfying(link -> assertThat(linkRepository.checkConnected(link.getId(), 1)).isTrue());
    }

    @Test
    @Transactional
    @Rollback
    public void makeConnectedTest() {
        assertThat(linkRepository.findByURL(links.get(6))).isPresent().hasValueSatisfying(link -> {
            linkRepository.makeConnected(link.getId(), 1);
            assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chat_link WHERE chat_id = 1 AND link_id = ?",
                Integer.class,
                link.getId()
            )).isEqualTo(1);
            assertThat(linkRepository.checkConnected(link.getId(), 1)).isTrue();
        });
    }

    @Test
    @Transactional
    @Rollback
    public void findAllTest() {
        assertThat(linkRepository.findAll()).extracting(Link::getUrl)
            .containsExactlyInAnyOrder(links.toArray(String[]::new));
    }

    @Test
    @Transactional
    @Rollback
    public void updateTest() {
        JdbcLink newLink = new JdbcLink(-1, "url", OffsetDateTime.now().minusHours(10), OffsetDateTime.now());
        jdbcTemplate.update(
            "INSERT INTO link (url, last_check_time, created_at) VALUES (?, ?, ?)",
            newLink.getUrl(),
            newLink.getLastCheckTime(),
            newLink.getCreatedAt()
        );
        assertThat(linkRepository.findByURL("url")).isPresent().hasValueSatisfying(link -> {
            linkRepository.updateLastCheckTime(link.getId());
            assertThat(linkRepository.findByURL("url")).isPresent()
                .hasValueSatisfying(l -> assertThat(l.getLastCheckTime()).isAfter(newLink.getLastCheckTime()));
        });
    }

    @Test
    @Transactional
    @Rollback
    public void findOldestTest() {
        List<String> urls = List.of(
            "https://github.com/fessur/java-course-2023-backend",
            "https://github.com/dotnet/aspnetcore",
            "https://github.com/lobehub/lobe-chat",
            "https://github.com/lavague-ai/LaVague"
        );
        List.of(
            new JdbcLink(-1, urls.get(0), OffsetDateTime.now().minusHours(10), OffsetDateTime.now()),
            new JdbcLink(-1, urls.get(1), OffsetDateTime.now().minusHours(7), OffsetDateTime.now()),
            new JdbcLink(-1, urls.get(2), OffsetDateTime.now().minusHours(5), OffsetDateTime.now()),
            new JdbcLink(-1, urls.get(3), OffsetDateTime.now().minusHours(2), OffsetDateTime.now())
        ).forEach(link -> jdbcTemplate.update(
            "INSERT INTO link (url, last_check_time, created_at) VALUES (?, ?, ?)",
            link.getUrl(),
            link.getLastCheckTime(),
            link.getCreatedAt()
        ));

        assertThat(linkRepository.findOldest(Duration.ofHours(4))).extracting(Link::getUrl)
            .containsExactlyInAnyOrder(urls.get(0), urls.get(1), urls.get(2));
    }
}
