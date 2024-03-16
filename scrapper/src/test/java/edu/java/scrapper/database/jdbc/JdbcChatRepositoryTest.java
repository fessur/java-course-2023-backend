package edu.java.scrapper.database.jdbc;

import edu.java.repository.ChatRepository;
import edu.java.repository.dto.Chat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.*;

public class JdbcChatRepositoryTest extends JdbcBaseRepositoryTest {
    @Autowired
    private ChatRepository chatRepository;

    @Test
    @Transactional
    @Rollback
    public void addTest() {
        chatRepository.add(new Chat(6, null));
        assertThat(jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM chat WHERE id = 6",
            Integer.class
        )).isEqualTo(1);
    }

    @Test
    @Transactional
    @Rollback
    public void removeTest() {
        chatRepository.remove(4);
        assertThat(jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM chat WHERE id = 4",
            Integer.class
        )).isEqualTo(0);
        assertThat(jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM chat_link WHERE chat_id = 4",
            Integer.class
        )).isEqualTo(0);
    }

    @Test
    @Transactional
    @Rollback
    public void findTest() {
        assertThat(chatRepository.findById(1)).isPresent()
            .hasValueSatisfying(chat -> assertThat(chat).extracting(Chat::id).isEqualTo(1L));
        assertThat(chatRepository.findById(6)).isEmpty();
    }
}
