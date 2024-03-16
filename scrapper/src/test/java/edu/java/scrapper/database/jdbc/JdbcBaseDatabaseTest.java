package edu.java.scrapper.database.jdbc;

import edu.java.scrapper.database.IntegrationTest;
import edu.java.scrapper.database.jdbc.dto.ChatTestDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class JdbcBaseDatabaseTest extends IntegrationTest {
    @Autowired
    private DataSource dataSource;

    protected JdbcTemplate jdbcTemplate;
    protected final List<String> links = List.of(
        "https://stackoverflow.com/questions/927358",
        "https://stackoverflow.com/questions/2003505",
        "https://stackoverflow.com/questions/292357",
        "https://stackoverflow.com/questions/477816",
        "https://stackoverflow.com/questions/348170",
        "https://github.com/spring-projects/spring-framework",
        "https://github.com/hibernate/hibernate-orm"
    );
    protected final List<ChatTestDTO> initData = List.of(
        new ChatTestDTO(1, List.of(links.get(0), links.get(1), links.get(2))),
        new ChatTestDTO(2, List.of(links.get(1), links.get(2), links.get(3))),
        new ChatTestDTO(3, List.of(links.get(0), links.get(3), links.get(4), links.get(5))),
        new ChatTestDTO(4, List.of(links.get(1), links.get(3), links.get(6))),
        new ChatTestDTO(5, List.of())
    );

    @BeforeAll
    public void beforeAll() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        links.forEach(link -> {
            jdbcTemplate.update("INSERT INTO link (url) VALUES (?)", link);
        });
        initData.forEach(chat -> {
            jdbcTemplate.update("INSERT INTO chat (id) VALUES (?)", chat.id());
            chat.links().forEach(link -> {
                    jdbcTemplate.update(
                        "INSERT INTO chat_link (chat_id, link_id) VALUES (?, (SELECT id FROM link WHERE url = ?))",
                        chat.id(), link
                    );
                }
            );
        });
    }

    @AfterAll
    public void afterAll() {
        initData.forEach(chat -> {
            chat.links().forEach(link -> {
                jdbcTemplate.update(
                    "DELETE FROM chat_link WHERE chat_id = ? AND link_id IN (SELECT id FROM link WHERE url = ?)",
                    chat.id(),
                    link
                );
            });
            jdbcTemplate.update("DELETE FROM chat WHERE id = ?", chat.id());
        });
        links.forEach(link -> {
            jdbcTemplate.update("DELETE FROM link WHERE url = ?", link);
        });
    }
}
