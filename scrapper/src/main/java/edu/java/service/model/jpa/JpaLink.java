package edu.java.service.model.jpa;

import edu.java.service.model.Link;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "link")
public class JpaLink implements Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(name = "last_check_time", nullable = false, insertable = false)
    @Generated
    private OffsetDateTime lastCheckTime;

    @Column(name = "created_at", nullable = false, insertable = false)
    @Generated
    private OffsetDateTime createdAt;

    @ManyToMany(mappedBy = "links")
    private Set<JpaChat> chats = new HashSet<>();
}
