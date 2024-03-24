package edu.java.repository.jpa;

import edu.java.service.model.jpa.JpaLink;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaLinkRepository extends JpaRepository<JpaLink, Long> {
    Optional<JpaLink> findByUrl(String url);

    @Query("SELECT link FROM JpaLink link WHERE link.lastCheckTime < :threshold")
    Collection<JpaLink> findOldest(@Param("threshold") OffsetDateTime threshold);

    @Modifying
    @Query("UPDATE JpaLink link SET link.lastCheckTime = CURRENT_TIMESTAMP WHERE link.id = :id")
    void updateLastCheckTime(@Param("id") Long id);
}
