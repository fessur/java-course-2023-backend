package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.client.GithubClient;
import edu.java.client.dto.GithubRepositoryRequest;
import edu.java.client.implementation.GithubClientImpl;
import edu.java.client.retry.RetryConfiguration;
import edu.java.configuration.ApplicationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest(httpPort = 8091)
public class RetryTest {
    private static final String BASE_URL = "http://localhost:8091";
    private static final String OWNER = "torvalds";
    private static final String REPOSITORY = "linux";
    private final RetryConfiguration retryConfiguration = new RetryConfiguration();

    @BeforeEach
    void setUp() {
        stubFor(get(String.format("/repos/%s/%s", OWNER, REPOSITORY))
            .willReturn(aResponse().withStatus(500)));
    }

    @Test
    public void testRetryConstant() {
        GithubClient githubClient = new GithubClientImpl(
            BASE_URL,
            retryConfiguration.githubRetryTemplate(createApplicationConfig(
                new RetryBuilder(6, new int[] {500}).constant(300)))
        );

        timed(1500, () -> request(githubClient));
    }

    @Test
    public void testRetryLinear() {
        GithubClient githubClient = new GithubClientImpl(
            BASE_URL,
            retryConfiguration.githubRetryTemplate(createApplicationConfig(
                new RetryBuilder(10, new int[] {500})
                    .linear(100, 100, 20000)))
        );

        timed(4500, () -> request(githubClient));
    }

    @Test
    public void testRetryExponent() {
        GithubClient githubClient = new GithubClientImpl(
            BASE_URL,
            retryConfiguration.githubRetryTemplate(createApplicationConfig(
                new RetryBuilder(6, new int[] {500})
                    .exponent(100, 2, 20000)))
        );

        timed(3100, () -> request(githubClient));
    }

    private void request(GithubClient githubClient) {
        assertThat(githubClient.fetchRepository(new GithubRepositoryRequest(OWNER, REPOSITORY)))
            .isNotNull().isEmpty();
    }

    private void timed(long time, Runnable action) {
        long startTime = System.currentTimeMillis();
        action.run();
        long endTime = System.currentTimeMillis();
        assertThat(endTime - startTime).isGreaterThan(time);
    }

    private ApplicationConfig createApplicationConfig(ApplicationConfig.Retry retry) {
        return new ApplicationConfig(null, new ApplicationConfig.Clients(
            new ApplicationConfig.Github(
                BASE_URL, retry
            ), null, null
        ), null, null);
    }
}
