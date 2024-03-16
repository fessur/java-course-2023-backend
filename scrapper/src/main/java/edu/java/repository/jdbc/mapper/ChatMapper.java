package edu.java.repository.jdbc.mapper;

import edu.java.repository.dto.Chat;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ChatMapper implements RowMapper<Chat> {
    @Override
    public Chat mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        Date createdAt = rs.getDate("created_at");
        return new Chat(id, createdAt);
    }
}
