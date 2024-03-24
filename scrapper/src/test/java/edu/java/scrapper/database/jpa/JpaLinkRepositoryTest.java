package edu.java.scrapper.database.jpa;

import edu.java.repository.jpa.JpaChatRepository;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.service.model.jpa.JpaChat;
import edu.java.service.model.jpa.JpaLink;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;

public class JpaLinkRepositoryTest extends JpaBaseDatabaseTest {
    @Autowired
    private JpaLinkRepository linkRepository;

    @Autowired
    private JpaChatRepository chatRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    @Rollback
    public void testFind() {
        Optional<JpaLink> optLink = linkRepository.findByUrl(links.getFirst());
        assertThat(optLink).isPresent();
        JpaLink link = optLink.get();
        assertThat(link).extracting(JpaLink::getCreatedAt, JpaLink::getLastCheckTime).doesNotContainNull();
        assertThat(link.getChats()).extracting(JpaChat::getId).containsExactlyInAnyOrder(1L, 3L);

        assertThat(linkRepository.findByUrl(newLinks.getFirst())).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    public void testSave() {
        JpaLink link = new JpaLink();
        link.setUrl(newLinks.getFirst());
        JpaLink saved = linkRepository.save(link);
        entityManager.flush();
        entityManager.refresh(link);

        assertThat(saved).isNotNull();
        assertThat(saved).extracting(JpaLink::getLastCheckTime, JpaLink::getCreatedAt).doesNotContainNull();
        assertThat(saved.getChats()).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @Sql(scripts = "classpath:/sql/add_old_links.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:/sql/delete_old_links.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testOld() {
        Collection<JpaLink> old = linkRepository.findOldest(OffsetDateTime.now().minus(Duration.ofHours(3)));
        assertThat(old).extracting(JpaLink::getUrl)
            .containsExactlyInAnyOrder(oldest.get(1), oldest.get(2), oldest.get(3));
        Optional<JpaLink> optLink1 = linkRepository.findByUrl(oldest.getFirst());
        assertThat(optLink1).isPresent();
        linkRepository.updateLastCheckTime(optLink1.get().getId());
        Optional<JpaLink> optLink2 = linkRepository.findByUrl(oldest.getFirst());
        assertThat(optLink2).isPresent();
        entityManager.flush();
        entityManager.refresh(optLink2.get());
        assertThat(optLink2.get().getLastCheckTime()).isAfter(OffsetDateTime.now().minus(Duration.ofMinutes(1)));
    }
}
