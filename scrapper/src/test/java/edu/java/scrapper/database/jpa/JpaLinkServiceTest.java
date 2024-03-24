package edu.java.scrapper.database.jpa;

import edu.java.repository.jpa.JpaChatRepository;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.service.exception.LinkAlreadyTrackingException;
import edu.java.service.exception.NoSuchChatException;
import edu.java.service.exception.NoSuchLinkException;
import edu.java.service.jpa.JpaChatService;
import edu.java.service.jpa.JpaLinkService;
import edu.java.service.model.jpa.JpaChat;
import edu.java.service.model.jpa.JpaLink;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.*;

public class JpaLinkServiceTest extends JpaBaseDatabaseTest {
    @Autowired
    private JpaLinkService linkService;

    @Autowired
    private JpaChatService chatService;

    @Autowired
    private JpaLinkRepository linkRepository;

    @Autowired
    private JpaChatRepository chatRepository;

    @Test
    @Transactional
    @Rollback
    public void testAdd() {
        assertThatNoException().isThrownBy(() -> {
            linkService.add(newLinks.getFirst(), 1L);

            Optional<JpaLink> optLink = linkRepository.findByUrl(newLinks.getFirst());
            assertThat(optLink).isPresent();
            JpaLink link = optLink.get();
            assertThat(link).extracting(JpaLink::getCreatedAt, JpaLink::getLastCheckTime).doesNotContainNull();
            assertThat(link.getChats()).extracting(JpaChat::getId).containsExactly(1L);

            Optional<JpaChat> optChat = chatRepository.findById(1L);
            assertThat(optChat).isPresent();
            JpaChat chat = optChat.get();
            assertThat(chat.getLinks()).extracting(JpaLink::getUrl).contains(newLinks.getFirst());
        });
    }

    @Test
    @Transactional
    @Rollback
    public void testAddNoChat() {
        assertThatExceptionOfType(NoSuchChatException.class).isThrownBy(() -> {
            linkService.add(newLinks.getFirst(), 6L);
        });
    }

    @Test
    @Transactional
    @Rollback
    public void testAddAlreadyTracking() {
        assertThatExceptionOfType(LinkAlreadyTrackingException.class).isThrownBy(() -> {
            linkService.add(links.getFirst(), 1L);
        });
    }

    @Test
    @Transactional
    @Rollback
    public void testRemove() {
        assertThatNoException().isThrownBy(() -> {
            linkService.remove(links.get(6), 4L);

            Optional<JpaLink> optLink1 = linkRepository.findByUrl(links.get(6));
            assertThat(optLink1).isPresent();
            JpaLink link1 = optLink1.get();
            assertThat(link1.getChats()).isEmpty();

            Optional<JpaChat> optChat1 = chatRepository.findById(4L);
            assertThat(optChat1).isPresent();
            JpaChat chat1 = optChat1.get();
            assertThat(chat1.getLinks()).extracting(JpaLink::getUrl).doesNotContain(links.get(6));

            linkService.remove(links.get(1), 4L);
            linkService.remove(links.get(3), 4L);

            Optional<JpaLink> optLink2 = linkRepository.findByUrl(links.get(1));
            assertThat(optLink2).isPresent();
            JpaLink link2 = optLink1.get();
            assertThat(link2.getChats()).extracting(JpaChat::getId).doesNotContain(4L);

            Optional<JpaLink> optLink3 = linkRepository.findByUrl(links.get(3));
            assertThat(optLink3).isPresent();
            JpaLink link3 = optLink1.get();
            assertThat(link3.getChats()).extracting(JpaChat::getId).doesNotContain(4L);

            Optional<JpaChat> optChat2 = chatRepository.findById(4L);
            assertThat(optChat2).isPresent();
            JpaChat chat2 = optChat1.get();
            assertThat(chat2.getLinks()).isEmpty();
        });
    }

    @Test
    @Transactional
    @Rollback
    public void testRemoveNoChat() {
        assertThatExceptionOfType(NoSuchChatException.class).isThrownBy(() -> {
            linkService.remove(links.getFirst(), 6L);
        });
    }

    @Test
    @Transactional
    @Rollback
    public void testRemoveNotTracked() {
        assertThatExceptionOfType(NoSuchLinkException.class).isThrownBy(() -> {
            linkService.remove(links.get(3), 1L);
        });
    }

    @Test
    @Transactional
    @Rollback
    public void testListAll() {
        assertThatNoException().isThrownBy(() -> {
            assertThat(linkService.listAll(1L)).extracting(JpaLink::getUrl)
                .containsExactlyInAnyOrder(links.get(0), links.get(1), links.get(2));
        });
    }

    @Test
    @Transactional
    @Rollback
    public void testListAllNoChat() {
        assertThatExceptionOfType(NoSuchChatException.class).isThrownBy(() -> {
            linkService.listAll(6L);
        });
    }

    @Test
    @Transactional
    @Rollback
    void complicatedTest() {
        assertThatNoException().isThrownBy(() -> {
            chatService.register(6L);
            chatService.register(7L);
            JpaLink link1 = linkService.add(newLinks.get(0), 6L);

            assertThat(link1).isNotNull().extracting(JpaLink::getUrl).isEqualTo(newLinks.get(0));
            assertThat(link1).extracting(JpaLink::getLastCheckTime, JpaLink::getCreatedAt).doesNotContainNull();
            assertThat(link1.getChats()).isNotNull().extracting(JpaChat::getId).containsExactly(6L);

            JpaLink link2 = linkService.add(newLinks.get(0), 7L);
            assertThat(link2).isNotNull().extracting(JpaLink::getUrl).isEqualTo(newLinks.get(0));
            assertThat(link2).extracting(JpaLink::getLastCheckTime, JpaLink::getCreatedAt).doesNotContainNull();
            assertThat(link2.getChats()).isNotNull().extracting(JpaChat::getId).containsExactlyInAnyOrder(6L, 7L);

            JpaLink link3 = linkService.add(newLinks.get(1), 6L);
            assertThat(link3).isNotNull().extracting(JpaLink::getUrl).isEqualTo(newLinks.get(1));
            assertThat(link3).extracting(JpaLink::getLastCheckTime, JpaLink::getCreatedAt).doesNotContainNull();
            assertThat(link3.getChats()).isNotNull().extracting(JpaChat::getId).containsExactly(6L);

            linkService.remove(newLinks.get(1), 6L);
            assertThat(linkService.listAll(6L)).extracting(JpaLink::getUrl).containsExactly(newLinks.get(0));

            chatService.unregister(6L);
            assertThat(linkService.listAll(7L)).isNotNull().extracting(JpaLink::getChats).flatExtracting(
                    chats -> chats.stream().map(JpaChat::getId).collect(Collectors.toSet()))
                .containsExactly(7L);
            chatService.unregister(7L);

            Optional<JpaLink> optRemain = linkRepository.findByUrl(newLinks.get(0));
            assertThat(optRemain).isPresent();
            JpaLink remain = optRemain.get();
            assertThat(remain.getChats()).isEmpty();
        });
    }
}
