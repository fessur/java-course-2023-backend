package edu.java.service.model.jdbc;

import edu.java.service.model.Chat;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JdbcChat implements Chat {
    private final long id;
    private final OffsetDateTime createdAt;
}
