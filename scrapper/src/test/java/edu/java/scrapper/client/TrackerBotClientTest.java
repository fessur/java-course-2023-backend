package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.client.TrackerBotClient;
import edu.java.client.exception.BadRequestException;
import edu.java.client.implementation.TrackerBotClientImpl;
import edu.java.configuration.ApplicationConfig;
import edu.java.client.retry.RetryConfiguration;
import edu.java.service.model.Link;
import edu.java.service.model.jdbc.JdbcLink;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

@WireMockTest(httpPort = 8083)
public class TrackerBotClientTest {

    private static final String BASE_URL = "http://localhost:8083";

    private TrackerBotClient trackerBotClient;

    @BeforeEach
    public void setUp() {
        trackerBotClient = new TrackerBotClientImpl(
            BASE_URL,
            new RetryConfiguration().trackerBotRetrySpec(createApplicationConfig())
        );
    }

    @Test
    public void testSendUpdates() {
        Link link = new JdbcLink(
            10,
            "https://stackoverflow.com/questions/41609436/powermock-after-log4j2-3-upgrade-could-not-reconfigure-jmx-java-lang-linkageerro",
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
        String description = "New answer appeared!";
        List<Long> chatIds = List.of(123456789L);

        stubFor(post("/updates")
            .withRequestBody(equalToJson(createJson(link, description, chatIds)))
            .willReturn(ok()));

        assertThatNoException().isThrownBy(() -> trackerBotClient.sendUpdate(link, description, chatIds));
    }

    @Test
    public void testInvalidUpdate() {
        Link link = new JdbcLink(
            10,
            "not-a-valid-link",
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
        String description = "New answer appeared!";
        List<Long> chatIds = List.of(123456789L);

        stubFor(post("/updates")
            .withRequestBody(equalToJson(createJson(link, description, chatIds)))
            .willReturn(badRequest().withHeader("Content-Type", "application/json")
                .withBody(createApiErrorResponseJson())));

        assertThatExceptionOfType(BadRequestException.class).isThrownBy(() -> trackerBotClient.sendUpdate(
            link,
            description,
            chatIds
        ));
    }

    private String createJson(Link link, String description, List<Long> chatIds) {
        return String.format("""
            {
                "id": %d,
                "url": "%s",
                "description": "%s",
                "tgChatIds": [
                    %d
                ]
            }
            """, link.getId(), link.getUrl(), description, chatIds.getFirst());
    }

    private String createApiErrorResponseJson() {
        return """
            {
                "description": "Invalid request parameters",
                  "code": "400",
                  "exceptionName": "",
                  "exceptionMessage": "",
                  "stacktrace": [
                    "stacktrace"
                  ]
            }
            """;
    }

    private ApplicationConfig createApplicationConfig() {
        return new ApplicationConfig(null, new ApplicationConfig.Clients(null, null,
            new ApplicationConfig.TrackerBot(
                BASE_URL,
                new RetryBuilder(1, new int[] {500}).constant(0)
            )
        ), null, null);
    }
}
