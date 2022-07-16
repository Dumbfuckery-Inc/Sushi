package lol.arikatsu.sushi.objects;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public final class DebugLogFilter extends Filter<ILoggingEvent> {
    @Override public FilterReply decide(ILoggingEvent event) {
        // Check if the event should be logged.
        if(DiscordLogger.shouldLog()) {
            // Send the message.
            DiscordLogger.logToChannel(event.getFormattedMessage());
        }

        return FilterReply.NEUTRAL; // Always let the configuration decide.
    }
}