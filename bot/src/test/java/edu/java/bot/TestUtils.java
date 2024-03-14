package edu.java.bot;

import com.pengrad.telegrambot.request.SendMessage;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import static org.assertj.core.api.Assertions.*;

public final class TestUtils {
    private TestUtils() {}

    public static void checkMessage(SendMessage sendMessage, String message) {
        assertThat(sendMessage).isNotNull().extracting(SendMessage::getParameters).isNotNull()
            .extracting(p -> p.get("text")).isNotNull().isEqualTo(message);
    }

    public static URL toUrl(String link) {
        try {
            return URI.create(link).toURL();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
