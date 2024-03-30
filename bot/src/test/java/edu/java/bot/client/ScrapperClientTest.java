package edu.java.bot.client;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.bot.TestUtils;
import edu.java.bot.client.dto.LinkResponse;
import edu.java.bot.client.dto.ListLinksResponse;
import edu.java.bot.client.exception.BadRequestException;
import edu.java.bot.client.exception.ConflictException;
import edu.java.bot.client.exception.NotFoundException;
import edu.java.bot.client.implementation.ScrapperClientImpl;
import edu.java.bot.client.retry.RetryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

@WireMockTest(httpPort = 8089)
public class ScrapperClientTest {
    private ScrapperClient scrapperClient;
    private static final String CHAT_URL = "/tg-chat";
    private static final String LINKS_URL = "/links";
    private static final String CHAT_ID_HEADER = "Tg-Chat-Id";
    private static final List<String> LINKS = List.of(
        "https://stackoverflow.com/questions/927358/how-do-i-undo-the-most-recent-local-commits-in-git",
        "https://stackoverflow.com/questions/2003505/how-do-i-delete-a-git-branch-locally-and-remotely",
        "https://stackoverflow.com/questions/292357/what-is-the-difference-between-git-pull-and-git-fetch",
        "https://stackoverflow.com/questions/477816/which-json-content-type-do-i-use",
        "https://stackoverflow.com/questions/348170/how-do-i-undo-git-add-before-commit",
        "https://github.com/spring-projects/spring-framework",
        "https://github.com/hibernate/hibernate-orm",
        "https://junit.org/junit5/docs/current/user-guide/#writing-tests-annotations",
        "https://hub.docker.com/_/postgres",
        "https://www.google.com/"
    );

    @BeforeEach
    void setUp() {
        scrapperClient = new ScrapperClientImpl("http://localhost:8089",
            new RetryConfiguration().retryTemplate(new RetryBuilder(1, new int[]{500}).constant(0)));
    }

    @Test
    public void testRegisterChat() {
        long chatId = new Random().nextLong();
        stubFor(post(CHAT_URL + "/" + chatId).willReturn(ok()));
        assertThatNoException().isThrownBy(() -> scrapperClient.registerChat(chatId));
    }

    @Test
    public void testRegisterAgain() {
        long chatId = new Random().nextLong();
        stubFor(post(CHAT_URL + "/" + chatId).willReturn(ok()));
        assertThatNoException().isThrownBy(() -> scrapperClient.registerChat(chatId));

        stubFor(post(CHAT_URL + "/" + chatId).willReturn(aResponse().withStatus(409)
            .withHeader("Content-Type", "application/json")
            .withBody(createApiErrorResponse("Chat is already registered", "409"))));
        assertThatExceptionOfType(ConflictException.class).isThrownBy(() -> scrapperClient.registerChat(chatId));
    }

    @Test
    public void testRegisterTwoChats() {
        long chatId1 = new Random().nextLong();
        stubFor(post(CHAT_URL + "/" + chatId1).willReturn(ok()));
        assertThatNoException().isThrownBy(() -> scrapperClient.registerChat(chatId1));

        long chatId2 = new Random().nextLong();
        stubFor(post(CHAT_URL + "/" + chatId2).willReturn(ok()));
        assertThatNoException().isThrownBy(() -> scrapperClient.registerChat(chatId2));
    }

    @Test
    public void testFetchLinks() {
        long chatId = new Random().nextLong();
        int size = 3;
        List<Long> linkIds = IntStream.range(0, size).mapToObj(index -> new Random().nextLong()).toList();
        stubFor(get(LINKS_URL).withHeader(CHAT_ID_HEADER, equalTo(Long.toString(chatId)))
            .willReturn(ok()
                .withHeader("Content-Type", "application/json")
                .withBody(createFetchLinksResponse(
                    linkIds,
                    LINKS.subList(0, size)
                )))
        );

        ListLinksResponse response = scrapperClient.fetchLinks(chatId);
        ListLinksResponse expectedResponse = new ListLinksResponse(IntStream.range(0, size)
            .mapToObj(index -> new LinkResponse(linkIds.get(index), TestUtils.toUrl(LINKS.get(index)))).toList(), size);
        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    public void testFetchLinksNoChat() {
        long chatId = new Random().nextLong();
        stubFor(get(LINKS_URL).withHeader(CHAT_ID_HEADER, equalTo(Long.toString(chatId)))
            .willReturn(notFound()
                .withHeader("Content-Type", "application/json")
                .withBody(createApiErrorResponse("Chat not found", "404")))
        );
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> scrapperClient.fetchLinks(chatId));
    }

    @Test
    public void testAddLink() {
        long chatId = new Random().nextLong();
        long linkId = new Random().nextLong();
        stubFor(post(LINKS_URL).withHeader(CHAT_ID_HEADER, equalTo(Long.toString(chatId)))
            .withRequestBody(equalToJson(createLinkRequest(LINKS.getFirst())))
            .willReturn(ok()
                .withHeader("Content-Type", "application/json")
                .withBody(createLinkResponse(linkId, LINKS.getFirst()))));
        assertThat(scrapperClient.trackLink(chatId, LINKS.getFirst())).extracting(
            LinkResponse::id,
            LinkResponse::url
        ).containsExactly(linkId, TestUtils.toUrl(LINKS.getFirst()));
    }

    @Test
    public void testAddLinkAlreadyTracking() {
        long chatId = new Random().nextLong();
        stubFor(post(LINKS_URL).withHeader(CHAT_ID_HEADER, equalTo(Long.toString(chatId)))
            .withRequestBody(equalToJson(createLinkRequest(LINKS.getFirst())))
            .willReturn(aResponse().withStatus(409).withHeader("Content-Type", "application/json")
                .withBody(createApiErrorResponse("Link is already tracking", "409")))
        );
        assertThatExceptionOfType(ConflictException.class).isThrownBy(() -> scrapperClient.trackLink(
            chatId,
            LINKS.getFirst()
        ));
    }

    @Test
    public void testAddLinkNoChat() {
        long chatId = new Random().nextLong();
        stubFor(post(LINKS_URL).withHeader(CHAT_ID_HEADER, equalTo(Long.toString(chatId)))
            .withRequestBody(equalToJson(createLinkRequest(LINKS.getFirst())))
            .willReturn(notFound().withHeader("Content-Type", "application/json")
                .withBody(createApiErrorResponse("Chat not found", "404")))
        );
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> scrapperClient.trackLink(
            chatId,
            LINKS.getFirst()
        ));
    }

    @Test
    public void testAddLinkInvalid() {
        long chatId = new Random().nextLong();
        String invalidLink = "not-a-valid-link";
        stubFor(post(LINKS_URL).withHeader(CHAT_ID_HEADER, equalTo(Long.toString(chatId)))
            .withRequestBody(equalToJson(createLinkRequest(invalidLink)))
            .willReturn(badRequest().withHeader("Content-Type", "application/json")
                .withBody(createApiErrorResponse("Invalid request parameters", "400")))
        );
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(() -> scrapperClient.trackLink(
            chatId,
            invalidLink
        ));
    }

    @Test
    public void testRemoveLink() {
        long chatId = new Random().nextLong();
        long linkId = new Random().nextLong();
        stubFor(delete(LINKS_URL).withHeader(CHAT_ID_HEADER, equalTo(Long.toString(chatId)))
            .withRequestBody(equalToJson(createLinkRequest(LINKS.getFirst())))
            .willReturn(ok()
                .withHeader("Content-Type", "application/json")
                .withBody(createLinkResponse(linkId, LINKS.getFirst()))));
        assertThat(scrapperClient.untrackLink(chatId, LINKS.getFirst())).extracting(
            LinkResponse::id,
            LinkResponse::url
        ).containsExactly(linkId, TestUtils.toUrl(LINKS.getFirst()));
    }

    @Test
    public void testRemoveLinkNoChat() {
        long chatId = new Random().nextLong();
        stubFor(delete(LINKS_URL).withHeader(CHAT_ID_HEADER, equalTo(Long.toString(chatId)))
            .withRequestBody(equalToJson(createLinkRequest(LINKS.getFirst())))
            .willReturn(notFound().withHeader("Content-Type", "application/json")
                .withBody(createApiErrorResponse("Chat not found", "404")))
        );
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> scrapperClient.untrackLink(
            chatId,
            LINKS.getFirst()
        ));
    }

    @Test
    public void testRemoveLinkNoLink() {
        long chatId = new Random().nextLong();
        stubFor(delete(LINKS_URL).withHeader(CHAT_ID_HEADER, equalTo(Long.toString(chatId)))
            .withRequestBody(equalToJson(createLinkRequest(LINKS.getFirst())))
            .willReturn(notFound().withHeader("Content-Type", "application/json")
                .withBody(createApiErrorResponse("The link is not tracking by this chat", "404")))
        );
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> scrapperClient.untrackLink(
            chatId,
            LINKS.getFirst()
        ));
    }

    @Test
    public void testRemoveLinkInvalid() {
        long chatId = new Random().nextLong();
        String invalidLink = "not-a-valid-link";
        stubFor(delete(LINKS_URL).withHeader(CHAT_ID_HEADER, equalTo(Long.toString(chatId)))
            .withRequestBody(equalToJson(createLinkRequest(invalidLink)))
            .willReturn(badRequest().withHeader("Content-Type", "application/json")
                .withBody(createApiErrorResponse("Invalid request parameters", "400")))
        );
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(() -> scrapperClient.untrackLink(
            chatId,
            invalidLink
        ));
    }

    private String createApiErrorResponse(String description, String code) {
        return String.format("""
            {
              "description": "%s",
              "code": "%s",
              "exceptionName": "",
              "exceptionMessage": "",
              "stacktrace": [
                "stacktrace"
              ]
            }
            """, description, code);
    }

    private String createFetchLinksResponse(List<Long> ids, List<String> links) {
        return String.format(
            """
                {
                  "links": [
                    %s
                  ],
                  "size": %d
                }
                """,
            IntStream.range(0, ids.size()).mapToObj(index -> createLinkResponse(ids.get(index), LINKS.get(index)))
                .collect(Collectors.joining(",\n")),
            links.size()
        );
    }

    private String createLinkRequest(String link) {
        return String.format("""
            {
              "link": "%s"
            }
            """, link);
    }

    private String createLinkResponse(long id, String link) {
        return String.format(
            """
                {
                    "id": %d,
                    "url": "%s"
                }""", id, link);
    }
}
