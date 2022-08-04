package lol.arikatsu.sushi.listeners;

import lol.arikatsu.sushi.annotations.BotListener;
import lol.arikatsu.sushi.managers.SnipeManager;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@BotListener
public final class MessageListener extends ListenerAdapter {
    @Override public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // Cache the message.
        SnipeManager.cacheMessage(event.getMessage());
    }

    @Override public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        // Update the snipe manager.
        SnipeManager.onMessageDeletion(event.getChannel(), event.getMessageId());
    }

    @Override public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        // Update the snipe manager.
        SnipeManager.onMessageEdit(event.getChannel(), event.getMessageId());
    }
}