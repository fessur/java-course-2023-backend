package edu.java.service.jpa;

import edu.java.configuration.ApplicationConfig;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.service.LinkUpdaterService;
import edu.java.service.domains.jpa.JpaDomain;
import edu.java.service.model.jpa.JpaLink;
import edu.java.util.CommonUtils;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class JpaLinkUpdaterService implements LinkUpdaterService {
    private final JpaLinkRepository linkRepository;
    private final ApplicationConfig applicationConfig;
    private final List<JpaDomain> domains;

    public JpaLinkUpdaterService(
        JpaLinkRepository linkRepository,
        ApplicationConfig applicationConfig,
        List<JpaDomain> domains
    ) {
        this.linkRepository = linkRepository;
        this.applicationConfig = applicationConfig;
        this.domains = domains;
    }

    @Override
    @Transactional
    public int update() {
        Collection<JpaLink> oldest =
            linkRepository.findOldest(OffsetDateTime.now().minus(applicationConfig.scheduler().forceCheckDelay()));
        oldest.forEach(link -> {
            if (link.getChats().isEmpty()) {
                linkRepository.delete(link);
            }
            linkRepository.updateLastCheckTime(link.getId());
            domains.stream().filter(d -> d.isValid(CommonUtils.toURL(link.getUrl()))).findFirst()
                .ifPresent(d -> d.update(link));
        });
        return oldest.size();
    }
}
