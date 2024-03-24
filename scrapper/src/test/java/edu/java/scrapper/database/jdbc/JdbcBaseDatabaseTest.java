package edu.java.scrapper.database.jdbc;

import edu.java.scrapper.database.BaseDatabaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import javax.sql.DataSource;

public abstract class JdbcBaseDatabaseTest extends BaseDatabaseTest {
    @Autowired
    private DataSource dataSource;

    protected JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jdbc");
    }

    @BeforeAll
    public void beforeAll() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
}
