package edu.java.scrapper.database.jdbc;

import edu.java.repository.ChatRepository;
import edu.java.repository.dto.Chat;
import edu.java.repository.jdbc.mapper.ChatMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import javax.sql.DataSource;
import static org.assertj.core.api.Assertions.*;

public class JdbcChatRepositoryTest extends JdbcBaseRepositoryTest {
    @Autowired
    private ChatRepository chatRepository;

    @Test
    @Transactional
    @Rollback
    public void addTest() {
        chatRepository.add(new Chat(7, null));
        chatRepository.add(new Chat(8, null));
        assertThat(jdbcTemplate.query("SELECT * FROM chat", new ChatMapper()))
            .extracting(Chat::id).containsExactlyInAnyOrder(1L, 2L, 3L, 4L, 5L, 6L, 7L);
    }

}
