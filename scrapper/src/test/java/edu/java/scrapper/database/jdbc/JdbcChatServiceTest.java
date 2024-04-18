package edu.java.scrapper.database.jdbc;

import edu.java.service.ChatService;
import edu.java.service.exception.ChatAlreadyRegisteredException;
import edu.java.service.exception.NoSuchChatException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class JdbcChatServiceTest extends JdbcBaseDatabaseTest {
    @Autowired
    private ChatService chatService;

    @Test
    @Transactional
    @Rollback
    public void testRegister() {
        chatService.register(6);
        assertThat(jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM chat WHERE id = 6",
            Integer.class
        )).isEqualTo(1);
    }

    @Test
    @Transactional
    @Rollback
    public void testAlreadyRegistered() {
        assertThatExceptionOfType(ChatAlreadyRegisteredException.class).isThrownBy(() -> chatService.register(1));
    }

    @Test
    @Transactional
    @Rollback
    public void testUnregister() {
        chatService.unregister(4);
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
    public void testNotRegistered() {
        assertThatExceptionOfType(NoSuchChatException.class).isThrownBy(() -> chatService.unregister(6));
    }
}
