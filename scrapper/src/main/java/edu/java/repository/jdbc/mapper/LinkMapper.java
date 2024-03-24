package edu.java.repository.jdbc.mapper;

import edu.java.service.model.jdbc.JdbcLink;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import org.springframework.jdbc.core.RowMapper;

public class LinkMapper implements RowMapper<JdbcLink> {
    @Override
    public JdbcLink mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        String url = rs.getString("url");
        OffsetDateTime lastCheckTime = rs.getObject("last_check_time", OffsetDateTime.class);
        OffsetDateTime createdAt = rs.getObject("created_at", OffsetDateTime.class);
        return new JdbcLink(id, url, lastCheckTime, createdAt);
    }
}
