package edu.java.scrapper.database.jdbc;

import edu.java.service.domain.Link;
import edu.java.service.LinkService;
import edu.java.service.exception.LinkAlreadyTrackingException;
import edu.java.service.exception.NoSuchChatException;
import edu.java.service.exception.NoSuchLinkException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class JdbcLinkServiceTest extends JdbcBaseDatabaseTest {
    @Autowired
    private LinkService linkService;

    @Test
    @Transactional
    @Rollback
    public void addSuccessTest() {
        String link = "https://github.com/tiangolo/full-stack-fastapi-template/blob/master/.github/FUNDING.yml";
        String normalized = "https://github.com/tiangolo/full-stack-fastapi-template";

        assertThat(linkService.add(link, 1)).extracting(Link::url).isEqualTo(normalized);

        assertThat(jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM link WHERE url = ?",
            Integer.class,
            normalized
        )).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM chat_link WHERE link_id IN (SELECT id FROM link WHERE url = ?)",
            Integer.class,
            normalized
        )).isEqualTo(1);
    }

    @Test
    @Transactional
    @Rollback
    public void addToUnregisteredTest() {
        assertThatExceptionOfType(NoSuchChatException.class).isThrownBy(() -> linkService.add(
            "https://github.com/NanmiCoder/MediaCrawler/blob/main/media_platform/xhs/help.py",
            6
        ));
    }

    @Test
    @Transactional
    @Rollback
    public void addAlreadyTrackingTest() {
        assertThatExceptionOfType(LinkAlreadyTrackingException.class).isThrownBy(() -> linkService.add(
            links.get(5),
            3
        ));
    }

    @Test
    @Transactional
    @Rollback
    public void removeSuccessTest() {
        Link link = linkService.remove(links.get(6), 4);
        assertThat(link.url()).isEqualTo(links.get(6));
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
    }

    @Test
    @Transactional
    @Rollback
    public void removeFromUnregisteredTest() {
        assertThatExceptionOfType(NoSuchChatException.class).isThrownBy(() -> linkService.remove(links.get(1), 6));
    }

    @Test
    @Transactional
    @Rollback
    public void removeUntrackedTest() {
        assertThatExceptionOfType(NoSuchLinkException.class).isThrownBy(() -> linkService.remove(links.getFirst(), 5));
    }

    @Test
    @Transactional
    @Rollback
    public void listAllTest() {
        assertThat(linkService.listAll(1)).extracting(Link::url)
            .containsExactlyInAnyOrder(links.get(0), links.get(1), links.get(2));
        assertThat(linkService.listAll(5)).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    public void listAllUnregistered() {
        assertThatExceptionOfType(NoSuchChatException.class).isThrownBy(() -> linkService.listAll(6));
    }
}
