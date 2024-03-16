package edu.java.scrapper.database;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

public class SimpleTest extends IntegrationTest {
    @Test
    public void testTablesExist() throws Exception {
        try (Connection conn = DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(),
            POSTGRES.getPassword()
        )) {
            try (Statement statement = conn.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM information_schema.tables WHERE table_name = 'chat';")) {
                    resultSet.next();
                    String tableName = resultSet.getString("table_name");
                    assertThat(tableName).isEqualToIgnoringCase("chat");
                }
                try (ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM information_schema.tables WHERE table_name = 'link';")) {
                    resultSet.next();
                    String tableName = resultSet.getString("table_name");
                    assertThat(tableName).isEqualToIgnoringCase("link");
                }
                try (ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM information_schema.tables WHERE table_name = 'chat_link';")) {
                    resultSet.next();
                    String tableName = resultSet.getString("table_name");
                    assertThat(tableName).isEqualToIgnoringCase("chat_link");
                }
            }
        }
    }
    @Test
    public void testColumnsExist() throws Exception {
        try (Connection conn = DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(),
            POSTGRES.getPassword()
        )) {
            try (Statement statement = conn.createStatement()) {
                try (ResultSet columns = statement.executeQuery(
                    "SELECT column_name FROM information_schema.columns WHERE table_name = 'chat';")) {
                    List<String> foundColumns = new ArrayList<>();
                    while (columns.next()) {
                        foundColumns.add(columns.getString("column_name"));
                    }
                    assertThat(foundColumns).containsExactlyInAnyOrder("id", "created_at");
                }
                try (ResultSet columns = statement.executeQuery(
                    "SELECT column_name FROM information_schema.columns WHERE table_name = 'link';")) {
                    List<String> foundColumns = new ArrayList<>();
                    while (columns.next()) {
                        foundColumns.add(columns.getString("column_name"));
                    }
                    assertThat(foundColumns).containsExactlyInAnyOrder("id", "url", "created_at", "last_check_time");
                }
                try (ResultSet columns = statement.executeQuery(
                    "SELECT column_name FROM information_schema.columns WHERE table_name = 'chat_link';")) {
                    List<String> foundColumns = new ArrayList<>();
                    while (columns.next()) {
                        foundColumns.add(columns.getString("column_name"));
                    }
                    assertThat(foundColumns).containsExactlyInAnyOrder("id", "chat_id", "link_id");
                }
            }
        }
    }
}
