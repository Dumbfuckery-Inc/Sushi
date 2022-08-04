package lol.arikatsu.sushi.managers;

import lol.arikatsu.sushi.objects.CachedMessage;
import lombok.Getter;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SnipeManager {
    /* Messages that have been sent. Should be cleared every 100. */
    @Getter private static final Map<String, CachedMessage> messages =
        new ConcurrentHashMap<>();

    /* Messages that have been deleted. */
    @Getter private static final Map<String, CachedMessage> deletedMessages =
        new ConcurrentHashMap<>();
    /* Messages that have been edited. */
    @Getter private static final Map<String, CachedMessage> editedMessages =
        new ConcurrentHashMap<>();

    private SnipeManager() {
        // This class is not meant to be instantiated.
    }

    /**
     * Caches a message into the snipe manager.
     * @param message The message to cache.
     */
    public static void cacheMessage(Message message) {
        // Remove past messages.
        if (messages.size() >= 100) {
            messages.clear();
        }

        // Cache the message.
        messages.put(message.getId(), CachedMessage.from(message));
    }

    /**
     * Updates the sniped message for the specified channel.
     * @param channel The channel to update.
     * @param messageId The message to update.
     */
    public static void onMessageDeletion(MessageChannel channel, String messageId) {
        // Get the message from the cache.
        var message = messages.get(messageId);
        // Check if the message is cached.
        if (message == null) {
            return;
        }

        // Remove the message from the cache.
        messages.remove(messageId);
        // Update the deleted messages.
        deletedMessages.put(channel.getId(), message);
    }

    /**
     * Updates the sniped message for the specified channel.
     * @param channel The channel to update.
     * @param messageId The message to update.
     */
    public static void onMessageEdit(MessageChannel channel, String messageId) {
        // Get the message from the cache.
        var message = messages.get(messageId);
        // Check if the message is cached.
        if (message == null) {
            return;
        }

        // Update the edited messages.
        editedMessages.put(channel.getId(), message);
    }

    /**
     * Returns the last deleted message in the channel.
     * @param channel The channel to get the message from.
     * @return The last deleted message in the channel.
     */
    public static CachedMessage getDeletedMessage(GuildChannel channel) {
        return deletedMessages.get(channel.getId());
    }

    /**
     * Returns the last edited message in the channel.
     * @param channel The channel to get the message from.
     * @return The last edited message in the channel.
     */
    public static CachedMessage getEditedMessage(GuildChannel channel) {
        return editedMessages.get(channel.getId());
    }
}