package edu.java.repository;

import edu.java.configuration.ApplicationConfig;
import edu.java.service.domain.Chat;
import edu.java.service.domain.Link;
import edu.java.service.exception.LinkAlreadyTrackingException;
import edu.java.service.exception.NoSuchLinkException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class LinkRepository extends BaseRepository {
    public LinkRepository(ApplicationConfig applicationConfig) {
        super(applicationConfig);
    }

    public Link addLink(long chatId, Link link) {
        Database database = readDatabase();
        Chat chat = findChat(database, chatId);
        find(chat, link).ifPresent(l -> {
            throw new LinkAlreadyTrackingException("Link is already tracking");
        });
        link.setId(database.getLinkCounter());
        database.incrementCounter();
        chat.addLink(link);
        writeDatabase(database);
        return link;
    }

    public List<Link> findAll(long chatId) {
        Database database = readDatabase();
        Chat chat = findChat(database, chatId);
        return chat.links();
    }

    public Link deleteLink(long chatId, Link link) {
        Database database = readDatabase();
        Chat chat = findChat(database, chatId);
        Optional<Link> foundLink = find(chat, link);
        if (foundLink.isPresent()) {
            chat.links().remove(foundLink.get());
            writeDatabase(database);
            return foundLink.get();
        } else {
            throw new NoSuchLinkException("Cannot find link");
        }
    }

    private Optional<Link> find(Chat chat, Link link) {
        List<Link> links = chat.links();
        return links.stream().filter(l -> l.getUrl().equals(link.getUrl())).findFirst();
    }
}
