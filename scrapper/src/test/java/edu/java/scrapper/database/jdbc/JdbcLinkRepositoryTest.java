package edu.java.scrapper.database.jdbc;

import edu.java.repository.LinkRepository;
import edu.java.service.domain.Link;
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
    private LinkRepository linkRepository;

    @Test
    @Transactional
    @Rollback
    public void addTest() {
        String url = "https://github.com/golang/go";
        linkRepository.add(new Link(-1, url, null, null), 1);

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
            .hasValueSatisfying(link -> assertThat(links.getFirst()).isEqualTo(link.url()));
    }

    @Test
    @Transactional
    @Rollback
    public void removeTest() {
        assertThat(linkRepository.findByURL(links.get(6))).isPresent().hasValueSatisfying(link -> {
            linkRepository.remove(link.id(), 4);
            assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chat_link WHERE chat_id = 4 AND link_id = ?",
                Integer.class,
                link.id()
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
        assertThat(linkRepository.findAllByChatId(1)).extracting(Link::url)
            .containsExactlyInAnyOrder(links.get(0), links.get(1), links.get(2));
        assertThat(linkRepository.findAllByChatId(5)).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    public void checkConnectedTest() {
        assertThat(linkRepository.findByURL(links.getFirst())).isPresent()
            .hasValueSatisfying(link -> assertThat(linkRepository.checkConnected(link.id(), 1)).isTrue());
    }

    @Test
    @Transactional
    @Rollback
    public void makeConnectedTest() {
        assertThat(linkRepository.findByURL(links.get(6))).isPresent().hasValueSatisfying(link -> {
            linkRepository.makeConnected(link.id(), 1);
            assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chat_link WHERE chat_id = 1 AND link_id = ?",
                Integer.class,
                link.id()
            )).isEqualTo(1);
            assertThat(linkRepository.checkConnected(link.id(), 1)).isTrue();
        });
    }

    @Test
    @Transactional
    @Rollback
    public void findAllTest() {
        assertThat(linkRepository.findAll()).extracting(Link::url)
            .containsExactlyInAnyOrder(links.toArray(String[]::new));
    }

    @Test
    @Transactional
    @Rollback
    public void updateTest() {
        Link newLink = new Link(-1, "url", OffsetDateTime.now().minusHours(10), OffsetDateTime.now());
        jdbcTemplate.update(
            "INSERT INTO link (url, last_check_time, created_at) VALUES (?, ?, ?)",
            newLink.url(),
            newLink.lastCheckTime(),
            newLink.createdAt()
        );
        assertThat(linkRepository.findByURL("url")).isPresent().hasValueSatisfying(link -> {
            linkRepository.updateLastCheckTime(link.id());
            assertThat(linkRepository.findByURL("url")).isPresent().hasValueSatisfying(l -> {
                assertThat(l.lastCheckTime()).isAfter(newLink.lastCheckTime());
            });
        });
    }

    @Test
    @Transactional
    @Rollback
    public void findOldestTest() {
        List<String> urls = List.of("https://github.com/fessur/java-course-2023-backend",
            "https://github.com/dotnet/aspnetcore",
            "https://github.com/lobehub/lobe-chat",
            "https://github.com/lavague-ai/LaVague"
        );
        List.of(
            new Link(-1, urls.get(0), OffsetDateTime.now().minusHours(10), OffsetDateTime.now()),
            new Link(-1, urls.get(1), OffsetDateTime.now().minusHours(7), OffsetDateTime.now()),
            new Link(-1, urls.get(2), OffsetDateTime.now().minusHours(5), OffsetDateTime.now()),
            new Link(-1, urls.get(3), OffsetDateTime.now().minusHours(2), OffsetDateTime.now())
        ).forEach(link -> {
            jdbcTemplate.update(
                "INSERT INTO link (url, last_check_time, created_at) VALUES (?, ?, ?)",
                link.url(),
                link.lastCheckTime(),
                link.createdAt()
            );
        });

        assertThat(linkRepository.findOldest(Duration.ofHours(4))).extracting(Link::url)
            .containsExactlyInAnyOrder(urls.get(0), urls.get(1), urls.get(2));
    }
}
