package edu.java.scrapper.database.jdbc;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.service.model.Link;
import edu.java.repository.jdbc.mapper.LinkMapper;
import edu.java.service.LinkUpdaterService;
import edu.java.service.model.jdbc.JdbcLink;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.*;

@WireMockTest(httpPort = 8090)
public class JdbcLinkUpdaterServiceTest extends JdbcBaseDatabaseTest {
    @Autowired
    private LinkUpdaterService linkUpdaterService;

    @Test
    @Transactional
    @Rollback
    public void updateTest() {
        stubFor(post("/updates").willReturn(ok()));
        List<String> urls = List.of("https://github.com/fessur/java-course-2023-backend",
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

        assertThat(linkUpdaterService.update()).isEqualTo(4);
        assertThat(jdbcTemplate.query("SELECT * FROM link", new LinkMapper())).extracting(Link::getLastCheckTime)
            .filteredOn(time -> time.isBefore(OffsetDateTime.now().minusMinutes(1))).isEmpty();
    }
}
