package edu.java.bot.util;

import edu.java.bot.domain.Link;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class CommonUtils {
    private CommonUtils() {
    }

    private static final Pattern PATTERN = Pattern.compile("\\s");

    public static <T> String joinEnumerated(List<T> list, int startNumber) {
        return IntStream.range(0, list.size())
            .mapToObj(index -> (index + startNumber) + ". " + list.get(index).toString())
            .collect(Collectors.joining("\n"));
    }

    public static String cutFirstWord(String text) {
        String trimmedText = text.trim();
        Matcher matcher = PATTERN.matcher(trimmedText);
        if (matcher.find()) {
            return trimmedText.substring(matcher.start() + 1);
        }
        return "";
    }

    public static Link parse(String url) throws URISyntaxException, MalformedURLException {
        URL parsed = new URI(url).toURL();
        return new Link(parsed.getHost(), url);
    }
}
