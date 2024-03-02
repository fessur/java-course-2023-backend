package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.client.TrackerBotClient;
import edu.java.client.exception.BadRequestException;
import edu.java.client.implementation.TrackerBotClientImpl;
import edu.java.service.domain.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

@WireMockTest(httpPort = 8083)
public class TrackerBotClientTest {

    private TrackerBotClient trackerBotClient;

    @BeforeEach
    public void setUp() {
        trackerBotClient = new TrackerBotClientImpl("http://localhost:8083");
    }

    @Test
    public void testSendUpdates() {
        Link link = new Link(
            10,
            "https://stackoverflow.com/questions/41609436/powermock-after-log4j2-3-upgrade-could-not-reconfigure-jmx-java-lang-linkageerro",
            "stackoverflow.com"
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
        Link link = new Link(
            10,
            "not-a-valid-link",
            "not-a-valid-domain"
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
}
