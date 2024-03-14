package edu.java.util;

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
}
