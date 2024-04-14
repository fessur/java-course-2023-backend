package edu.java.service;

import edu.java.service.site.Site;
import edu.java.util.CommonUtils;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SiteService {
    private final List<? extends Site> sites;

    public SiteService(List<? extends Site> sites) {
        this.sites = sites;
    }

    public Optional<String> validateLink(URL url) {
        return sites.stream().filter(d -> d.isValid(url)).findFirst().map(d -> {
            if (d.exists(url)) {
                return Optional.<String>empty();
            }
            return Optional.of(d.notExistsMessage());
        }).orElse(Optional.of(
            "Domain " + url.getHost() + " is not supported yet. List of all supported domains:\n"
                + CommonUtils.joinEnumerated(sites.stream().map(Site::toString).toList(), 1)));
    }

    public String normalizeLink(URL url) {
        return sites.stream().filter(d -> d.isValid(url)).findFirst().map(d -> d.normalize(url))
            .orElseThrow(() -> new IllegalArgumentException("The domain is not supported."));
    }
}
