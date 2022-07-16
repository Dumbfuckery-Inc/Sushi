package lol.arikatsu.sushi.objects;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.entities.MessageChannel;

import javax.annotation.Nullable;

@Accessors(fluent = true)
public final class DiscordLogger {
    @Setter @Getter private static boolean shouldLog = false;
    @Setter @Nullable private static MessageChannel channel = null;

    private DiscordLogger() {
        // This class is not meant to be instantiated.
    }

    /**
     * Logs the given message to the Discord channel.
     * @param toLog The message to log.
     */
    public static void logToChannel(String toLog) {
        // Check if the channel ID is set.
        if(channel == null) return;
        // Check if the channel is valid.
        if(!channel.canTalk()) {
            // Clear the channel and exit.
            channel = null; return;
        }

        try {
            // Send the message.
            channel.sendMessage("`" + toLog.replace('`', ' ') + "`").queue();
        } catch (Exception ignored) { }
    }
}