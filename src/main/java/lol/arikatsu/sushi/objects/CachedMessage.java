package lol.arikatsu.sushi.objects;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.time.OffsetDateTime;

public record CachedMessage(
    String messageContent,
    User author,
    OffsetDateTime timestamp
) {

    /**
     * Converts a message to a cached message.
     *
     * @param message The message to convert.
     * @return The cached message.
     */
    public static CachedMessage from(Message message) {
        return new CachedMessage(message.getContentRaw(), message.getAuthor(), message.getTimeCreated());
    }
}