package edu.java.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class CommonUtils {
    private CommonUtils() {
    }

    public static <T> String joinEnumerated(List<T> list, int startNumber) {
        return IntStream.range(0, list.size())
            .mapToObj(index -> (index + startNumber) + ". " + list.get(index).toString())
            .collect(Collectors.joining("\n"));
    }

    public static URL toURL(String url) {
        try {
             return new URI(url).toURL();
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
