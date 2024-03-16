package edu.java.repository.jdbc.mapper;

import edu.java.repository.dto.Chat;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class ChatMapper implements RowMapper<Chat> {
    @Override
    public Chat mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        OffsetDateTime createdAt = rs.getObject("created_at", OffsetDateTime.class);
        return new Chat(id, createdAt);
    }
}
