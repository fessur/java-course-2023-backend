package edu.java.service.model.jdbc;

import edu.java.service.model.Link;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JdbcLink implements Link {
    private final long id;
    private final String url;
    private final OffsetDateTime lastCheckTime;
    private final OffsetDateTime createdAt;
}
