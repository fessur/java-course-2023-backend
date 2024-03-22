package edu.java.service;

import edu.java.service.domains.Domain;
import edu.java.util.CommonUtils;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DomainService {
    private final List<Domain> domains;

    public DomainService(List<Domain> domains) {
        this.domains = domains;
    }

    public Optional<String> validateLink(URL url) {
        return domains.stream().filter(d -> d.isValid(url)).findFirst().map(d -> {
            if (d.exists(url)) {
                return Optional.<String>empty();
            }
            return Optional.of(d.notExistsMessage());
        }).orElse(Optional.of(
            "Domain " + url.getHost() + " is not supported yet. List of all supported domains:\n"
                + CommonUtils.joinEnumerated(domains.stream().map(Domain::toString).toList(), 1)));
    }

    public String normalizeLink(URL url) {
        return domains.stream().filter(d -> d.isValid(url)).findFirst().map(d -> d.normalize(url))
            .orElseThrow(() -> new IllegalArgumentException("The domain is not supported"));
    }
}
