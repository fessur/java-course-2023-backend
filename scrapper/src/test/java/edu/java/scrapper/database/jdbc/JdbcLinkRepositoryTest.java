package edu.java.scrapper.database.jdbc;

import edu.java.repository.LinkRepository;
import edu.java.repository.dto.Link;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.*;

public class JdbcLinkRepositoryTest extends JdbcBaseRepositoryTest {
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
}
