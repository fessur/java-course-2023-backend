package edu.java.service.jdbc;

import edu.java.configuration.props.ApplicationConfig;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.service.LinkUpdaterService;
import edu.java.service.model.jdbc.JdbcLink;
import edu.java.service.site.jdbc.JdbcSite;
import edu.java.util.CommonUtils;
import java.util.Collection;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class JdbcLinkUpdaterService implements LinkUpdaterService {
    private final JdbcLinkRepository linkRepository;
    private final ApplicationConfig applicationConfig;
    private final List<JdbcSite> domains;

    public JdbcLinkUpdaterService(
        JdbcLinkRepository linkRepository,
        ApplicationConfig applicationConfig,
        List<JdbcSite> domains
    ) {
        this.linkRepository = linkRepository;
        this.applicationConfig = applicationConfig;
        this.domains = domains;
    }

    @Override
    @Transactional
    public int update() {
        Collection<JdbcLink> oldest = linkRepository.findOldest(applicationConfig.scheduler().forceCheckDelay());
        oldest.forEach(link -> {
            linkRepository.updateLastCheckTime(link.getId());
            domains.stream().filter(d -> d.isValid(CommonUtils.toURL(link.getUrl()))).findFirst()
                .ifPresent(d -> d.update(link));
        });
        return oldest.size();
    }
}
