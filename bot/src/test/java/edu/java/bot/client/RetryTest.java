package edu.java.bot.client;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.bot.client.implementation.ScrapperClientImpl;
import edu.java.bot.client.retry.RetryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.*;

@WireMockTest(httpPort = 8092)
public class RetryTest {
    private static final String BASE_URL = "http://localhost:8092";
    private static final String CHAT_URL = "/tg-chat/1";
    private final RetryConfiguration retryConfiguration = new RetryConfiguration();

    @BeforeEach
    void setUp() {
        stubFor(post(CHAT_URL).willReturn(aResponse().withStatus(500)));
    }

    @Test
    public void testRetryConstant() {
        ScrapperClient scrapperClient = new ScrapperClientImpl(
            BASE_URL,
            retryConfiguration.retryTemplate(new RetryBuilder(6, new int[] {500}).constant(300))
        );

        timed(1500, () -> request(scrapperClient));
    }

    @Test
    public void testRetryLinear() {
        ScrapperClient scrapperClient = new ScrapperClientImpl(
            BASE_URL,
            retryConfiguration.retryTemplate(new RetryBuilder(10, new int[] {500})
                .linear(100, 100, 20000))
        );

        timed(4500, () -> request(scrapperClient));
    }

    @Test
    public void testRetryExponent() {
        ScrapperClient scrapperClient = new ScrapperClientImpl(
            BASE_URL,
            retryConfiguration.retryTemplate(new RetryBuilder(6, new int[] {500})
                .exponent(100, 2, 20000))
        );

        timed(3100, () -> request(scrapperClient));
    }

    private void request(ScrapperClient scrapperClient) {
        assertThatExceptionOfType(WebClientResponseException.class).isThrownBy(() ->
            scrapperClient.registerChat(1));
    }

    private void timed(long time, Runnable action) {
        long startTime = System.currentTimeMillis();
        action.run();
        long endTime = System.currentTimeMillis();
        assertThat(endTime - startTime).isGreaterThan(time);
    }
}
