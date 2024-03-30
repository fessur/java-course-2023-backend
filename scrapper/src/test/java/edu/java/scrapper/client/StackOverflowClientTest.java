package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.client.StackOverflowClient;
import edu.java.client.dto.StackOverflowPostInnerResponse;
import edu.java.client.implementation.StackOverflowClientImpl;
import edu.java.client.retry.RetryConfiguration;
import edu.java.configuration.ApplicationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

@WireMockTest(httpPort = 8082)
public class StackOverflowClientTest {

    private static final String BASE_URL = "http://localhost:8082";

    private StackOverflowClient stackOverflowClient;

    @BeforeEach
    public void setUp() {
        stackOverflowClient = new StackOverflowClientImpl(
            BASE_URL,
            new RetryConfiguration().stackOverflowRetryTemplate(createApplicationConfig())
        );
    }

    @Test
    public void testFetchPostSuccess() {
        final long postId = 8318911;

        setOkStub(postId);

        assertThat(stackOverflowClient.fetchPost(postId))
            .isPresent()
            .hasValueSatisfying(response ->
                assertThat(response)
                    .extracting(
                        StackOverflowPostInnerResponse::id,
                        StackOverflowPostInnerResponse::title,
                        StackOverflowPostInnerResponse::lastActivityDate
                    )
                    .containsExactly(
                        postId,
                        "Why does HTML think “chucknorris” is a color?",
                        OffsetDateTime.parse("2024-01-24T02:40:12Z")
                    )
            );
    }

    @Test
    public void testFetchPostFailed() {
        final long postId = 1;
        setEmptyStub(postId);
        assertThat(stackOverflowClient.fetchPost(postId)).isEmpty();
    }

    @Test
    public void testExistsSuccess() {
        final long postId = 8318911;
        setOkStub(postId);
        assertThat(stackOverflowClient.exists(postId)).isTrue();
    }

    @Test
    public void testNotExists() {
        final long postId = 1;
        setEmptyStub(postId);
        assertThat(stackOverflowClient.exists(postId)).isFalse();
    }

    private void setOkStub(long postId) {
        stubFor(get(String.format("/posts/%d?site=stackoverflow&filter=!nNPvSNOTRz", postId))
            .willReturn(ok()
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "items": [
                        {
                          "owner": {
                            "account_id": 206969,
                            "reputation": 87527,
                            "user_id": 456584,
                            "user_type": "registered",
                            "accept_rate": 88,
                            "profile_image": "https://i.stack.imgur.com/uoYDJ.png?s=256&g=1",
                            "display_name": "user456584",
                            "link": "https://stackoverflow.com/users/456584/user456584"
                          },
                          "score": 8783,
                          "last_edit_date": 1654501047,
                          "last_activity_date": 1706064012,
                          "creation_date": 1322607262,
                          "post_type": "question",
                          "post_id": 8318911,
                          "content_license": "CC BY-SA 4.0",
                          "title": "Why does HTML think “chucknorris” is a color?",
                          "link": "https://stackoverflow.com/q/8318911"
                        }
                      ],
                      "has_more": false,
                      "quota_max": 10000,
                      "quota_remaining": 9926
                    }""")));
    }

    private void setEmptyStub(long postId) {
        stubFor(get(String.format("/posts/%d?site=stackoverflow&filter=!nNPvSNOTRz", postId))
            .willReturn(ok()
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "items": [],
                      "has_more": false,
                      "quota_max": 10000,
                      "quota_remaining": 9953
                    }
                    """)));
    }

    private ApplicationConfig createApplicationConfig() {
        return new ApplicationConfig(null, new ApplicationConfig.Clients(null,
            new ApplicationConfig.StackOverflow(
                BASE_URL,
                new RetryBuilder(1, new int[] {500}).constant(0)
            ), null
        ), null, null);
    }
}
