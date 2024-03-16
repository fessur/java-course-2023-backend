package edu.java.repository.jdbc.mapper;

import edu.java.repository.dto.Link;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class LinkMapper implements RowMapper<Link> {
    @Override
    public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        String url = rs.getString("url");
        Date lastCheckTime = rs.getDate("last_check_time");
        Date createdAt = rs.getDate("created_at");
        return new Link(id, url, lastCheckTime, createdAt);
    }
}
