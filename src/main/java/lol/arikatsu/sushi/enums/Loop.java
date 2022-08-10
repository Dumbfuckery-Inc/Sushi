package lol.arikatsu.sushi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The player loop type.
 */
@Getter
@AllArgsConstructor
public enum Loop {
    /**
     * No loop.
     */
    NONE("Turned **off** repeat mode."),

    /**
     * Loop the current track.
     */
    CURRENT("Set the loop mode to **Track Loop**."),

    /**
     * Loop the entire queue.
     */
    QUEUE("Set the loop mode to **Queue Loop**.");

    final String message;
}