package lol.arikatsu.sushi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

import static lol.arikatsu.sushi.objects.Constants.COLOR_CONFIG;

/**
 * The type of embed to generate.
 */
@AllArgsConstructor @Getter
public enum EmbedType {
    /**
     * The generic embed.
     */
    SIMPLE(Color.decode(COLOR_CONFIG.defaultEmbedColor)),

    /**
     * The announcement embed.
     */
    ANNOUNCEMENT(Color.decode(COLOR_CONFIG.announcementEmbedColor)),

    /**
     * The embed used when an error occurs
     */
    ERROR(Color.decode(COLOR_CONFIG.errorEmbedColor));

    /* The color to use in the embed. */
    final Color embedColor;
}