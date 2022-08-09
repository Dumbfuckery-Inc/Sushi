package lol.arikatsu.sushi.objects;

/**
 * The result of a check.
 *
 * @param <T> The type of the result.
 */
public record Check<T>(boolean success, T result) {
}