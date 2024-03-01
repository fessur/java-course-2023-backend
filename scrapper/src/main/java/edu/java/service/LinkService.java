package edu.java.service;

import edu.java.repository.LinkRepository;
import edu.java.service.domain.Link;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LinkService {
    private final List<String> supportedDomains = List.of("github.com", "stackoverflow.com");
    private final LinkRepository linkRepository;

    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public List<String> getSupportedDomains() {
        return supportedDomains;
    }

    public boolean isSupported(String domain) {
        return supportedDomains.contains(domain);
    }

    public Link addLink(long id, Link link) {
        return linkRepository.addLink(id, link);
    }

    public Link deleteLink(long chatId, Link link) {
        return linkRepository.deleteLink(chatId, link);
    }

    public List<Link> findAll(long chatId) {
        return linkRepository.findAll(chatId);
    }

    public static Link parse(String url) {
        try {
            URL parsedUrl = new URI(url).toURL();
            return new Link(-1, parsedUrl.toString(), parsedUrl.getHost());
        } catch (MalformedURLException | URISyntaxException ex) {
            throw new AssertionError("Cannot come here.", ex);
        }
    }
}
