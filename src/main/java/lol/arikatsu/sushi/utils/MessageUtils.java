package lol.arikatsu.sushi.utils;

import lol.arikatsu.sushi.enums.EmbedType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public final class MessageUtils {
    private MessageUtils() {
        // This class is not meant to be instantiated.
    }

    /**
     * Makes a simple embed.
     * @param description The description of the embed.
     * @return The embed.
     */
    public static MessageEmbed makeEmbed(String description) {
        // Invoke MessageUtils#makeEmbed(String, EmbedType)
        return makeEmbed(description, EmbedType.SIMPLE);
    }

    /**
     * Makes an embed from a description and type.
     * @param description The description of the embed.
     * @param type The type of the embed.
     * @return The embed.
     */
    public static MessageEmbed makeEmbed(String description, EmbedType type) {
        return new EmbedBuilder()
            // Set the color of the embed.
            .setColor(type.getEmbedColor())
            // Set the description of the embed.
            .setDescription(description)
            // Build the embed.
            .build();
    }

    /**
     * Checks if a specified string is a string of numbers.
     * @param toCheck The string to check.
     * @param decimalCheck Should the string be allowed to contain a decimal?
     * @return True if the string is a string of numbers.
     */
    public static boolean isNumber(String toCheck, boolean decimalCheck) {
        try {
            // Check if the string is a decimal.
            if(toCheck.contains(".") && decimalCheck)
                Double.parseDouble(toCheck);
            else Long.parseLong(toCheck);
        } catch (NumberFormatException e) {
            // Return false if the string is not a number.
            return false;
        } return true; // Return true if the string is a number.
    }
}