package edu.java.scrapper.database.jpa;

import edu.java.repository.jpa.JpaChatRepository;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.service.exception.ChatAlreadyRegisteredException;
import edu.java.service.exception.NoSuchChatException;
import edu.java.service.jpa.JpaChatService;
import edu.java.service.model.jpa.JpaChat;
import edu.java.service.model.jpa.JpaLink;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;

public class JpaChatServiceTest extends JpaBaseDatabaseTest {
    @Autowired
    private JpaChatService chatService;

    @Autowired
    private JpaChatRepository chatRepository;

    @Autowired
    private JpaLinkRepository linkRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    @Rollback
    public void testRegister() {
        assertThatNoException().isThrownBy(() -> {
            chatService.register(6L);

            Optional<JpaChat> optChat = chatRepository.findById(6L);
            assertThat(optChat).isPresent();
            JpaChat chat = optChat.get();
            entityManager.flush();
            entityManager.refresh(chat);
            assertThat(chat.getId()).isEqualTo(6L);
            assertThat(chat.getCreatedAt()).isNotNull();
            assertThat(chat.getLinks()).isEmpty();
        });
    }

    @Test
    @Transactional
    @Rollback
    public void testAlreadyRegistered() {
        assertThatExceptionOfType(ChatAlreadyRegisteredException.class).isThrownBy(() -> {
            chatService.register(1L);
        });
    }

    @Test
    @Transactional
    @Rollback
    public void testUnregister() {
        assertThatNoException().isThrownBy(() -> {
            chatService.unregister(1L);

            assertThat(chatRepository.findById(1L)).isEmpty();
            Optional<JpaLink> optLink1 = linkRepository.findByUrl(links.getFirst());
            assertThat(optLink1).isPresent();
            JpaLink link1 = optLink1.get();
            assertThat(link1.getChats()).extracting(JpaChat::getId).containsExactly(3L);

            chatService.unregister(3L);
            assertThat(chatRepository.findById(1L)).isEmpty();
            Optional<JpaLink> optLink2 = linkRepository.findByUrl(links.getFirst());
            assertThat(optLink2).isPresent();
            JpaLink link2 = optLink2.get();
            assertThat(link2.getChats()).isEmpty();
        });
    }

    @Test
    @Transactional
    @Rollback
    public void testUnregisterNoChat() {
        assertThatExceptionOfType(NoSuchChatException.class).isThrownBy(() -> {
            chatService.unregister(6L);
        });
    }
}
