package lol.arikatsu.sushi.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class StringUtils {
    private StringUtils() {
        // This class should not be instantiated.
    }

    /**
     * Encodes a string to be used in a URL.
     * @param url The string to encode.
     * @return The encoded string.
     */
    public static String encodeUrl(String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }
}