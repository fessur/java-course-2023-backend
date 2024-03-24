package edu.java.scrapper.database.jpa;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.service.jpa.JpaLinkUpdaterService;
import edu.java.service.model.jpa.JpaLink;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.*;

@WireMockTest(httpPort = 8090)
public class JpaLinkUpdaterServiceTest extends JpaBaseDatabaseTest {
    @Autowired
    private JpaLinkUpdaterService linkUpdaterService;

    @Autowired
    private JpaLinkRepository linkRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    @Rollback
    @Sql(scripts = "classpath:/sql/add_old_links.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:/sql/delete_old_links.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testUpdate() {
        stubFor(post("/updates").willReturn(ok()));
        assertThat(linkUpdaterService.update()).isEqualTo(4);
        entityManager.flush();

        Optional<JpaLink> optLink1 = linkRepository.findByUrl(oldest.getFirst());
        assertThat(optLink1).isPresent();
        entityManager.refresh(optLink1.get());
        assertThat(optLink1.get().getLastCheckTime()).isAfter(OffsetDateTime.now().minus(Duration.ofMinutes(1)));

        Optional<JpaLink> optLink2 = linkRepository.findByUrl(oldest.get(1));
        assertThat(optLink2).isPresent();
        entityManager.refresh(optLink2.get());
        assertThat(optLink2.get().getLastCheckTime()).isAfter(OffsetDateTime.now().minus(Duration.ofMinutes(1)));

        Optional<JpaLink> optLink3 = linkRepository.findByUrl(oldest.get(2));
        assertThat(optLink3).isPresent();
        entityManager.refresh(optLink3.get());
        assertThat(optLink3.get().getLastCheckTime()).isAfter(OffsetDateTime.now().minus(Duration.ofMinutes(1)));

        assertThat(linkRepository.findByUrl(oldest.get(3))).isEmpty();
    }
}
