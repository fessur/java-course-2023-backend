package edu.java.scrapper.database.jpa;

import edu.java.scrapper.database.BaseDatabaseTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class JpaBaseDatabaseTest extends BaseDatabaseTest {
    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jpa");
    }
}
