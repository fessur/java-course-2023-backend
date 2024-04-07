package edu.java.scrapper.database;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = "classpath:/sql/init_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:/sql/clean_test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public abstract class BaseDatabaseTest extends IntegrationTest {
    protected final List<String> links = List.of(
        "https://stackoverflow.com/questions/927358",
        "https://stackoverflow.com/questions/2003505",
        "https://stackoverflow.com/questions/292357",
        "https://stackoverflow.com/questions/477816",
        "https://stackoverflow.com/questions/348170",
        "https://github.com/spring-projects/spring-framework",
        "https://github.com/hibernate/hibernate-orm"
    );

    protected final List<String> newLinks = List.of(
        "https://github.com/OpenInterpreter/open-interpreter",
        "https://github.com/microsoft/autogen"
    );

    protected final List<String> oldest = List.of(
        "https://github.com/mshumer/gpt-investor",
        "https://github.com/lewis-007/MediaCrawler",
        "https://github.com/lichao-sun/Mora",
        "https://github.com/jgthms/bulma"
    );

    @DynamicPropertySource
    static void dbProperties(DynamicPropertyRegistry registry) {
        registry.add("app.use-queue", () -> "false");
    }
}
