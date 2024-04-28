package edu.java.scrapper.database.jpa;

import edu.java.repository.jpa.JpaChatRepository;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.service.model.jpa.JpaChat;
import edu.java.service.model.jpa.JpaLink;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;

public class JpaChatRepositoryTest extends JpaBaseDatabaseTest {
    @Autowired
    private JpaChatRepository chatRepository;

    @Autowired
    private JpaLinkRepository linkRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    @Rollback
    public void testSave() {
        JpaChat chat1 = new JpaChat();
        chat1.setId(6L);
        JpaChat saved1 = chatRepository.save(chat1);
        entityManager.flush();
        assertThat(saved1).isNotNull();
        assertThat(saved1).extracting(JpaChat::getId).isEqualTo(6L);
        assertThat(saved1.getLinks()).isEmpty();
        assertThat(saved1.getCreatedAt()).isNotNull();

        Optional<JpaChat> optChat2 = chatRepository.findById(1L);
        assertThat(optChat2).isPresent();
        JpaChat chat2 = optChat2.get();
        Optional<JpaLink> optLink = linkRepository.findByUrl(links.get(3));
        assertThat(optLink).isPresent();
        JpaLink link = optLink.get();
        chat2.getLinks().add(link);
        link.getChats().add(chat2);
        entityManager.flush();
        entityManager.refresh(chat2);

        Optional<JpaChat> optSaved2 = chatRepository.findById(1L);
        assertThat(optSaved2).isPresent();
        JpaChat saved2 = optSaved2.get();
        assertThat(saved2.getLinks()).extracting(JpaLink::getUrl).contains(links.get(3));
    }

    @Test
    @Transactional
    @Rollback
    public void testFind() {
        assertThat(chatRepository.findById(6L)).isEmpty();
        Optional<JpaChat> chat1 = chatRepository.findById(1L);
        assertThat(chat1).isPresent();
        assertThat(chat1.get().getId()).isEqualTo(1L);
        assertThat(chat1.get().getCreatedAt()).isNotNull();
        assertThat(chat1.get().getLinks()).extracting(JpaLink::getUrl)
            .containsExactlyInAnyOrder(links.get(0), links.get(1), links.get(2));

        Optional<JpaChat> chat2 = chatRepository.findById(5L);
        assertThat(chat2).isPresent();
        assertThat(chat2.get().getId()).isEqualTo(5L);
        assertThat(chat2.get().getCreatedAt()).isNotNull();
        assertThat(chat2.get().getLinks()).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    public void testDelete() {
        chatRepository.deleteById(5L);
        assertThat(chatRepository.findById(5L)).isEmpty();
    }
}
