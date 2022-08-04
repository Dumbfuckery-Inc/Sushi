package lol.arikatsu.sushi.utils;

public final class MathUtils {
    private MathUtils() {
        // This class is not meant to be instantiated.
    }

    /**
     * Formats a timestamp to a readable format.
     * Format: 1d 2h 3m 4s
     * @param time The time to format.
     * @return The formatted timestamp.
     */
    public static String formatStopwatch(long time) {
        // Do math.
        long days = time / 86400000;
        long hours = (time % 86400000) / 3600000;
        long minutes = (time % 3600000) / 60000;
        long seconds = (time % 60000) / 1000;

        // Format the time.
        return "%dd %dh %dm %ds".formatted(days, hours, minutes, seconds);
    }

    /**
     * Returns a random number between the given minimum and maximum.
     * @param min The minimum number.
     * @param max The maximum number.
     * @return The random number.
     */
    public static int randomNumber(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }
}