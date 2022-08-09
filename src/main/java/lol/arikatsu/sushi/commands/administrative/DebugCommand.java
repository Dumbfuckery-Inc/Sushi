package lol.arikatsu.sushi.commands.administrative;

import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.objects.DiscordLogger;
import lol.arikatsu.sushi.utils.CommandUtils;
import lol.arikatsu.sushi.utils.MessageUtils;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

@BotCommand
public final class DebugCommand extends Command {
    public DebugCommand() {
        super("debug", "Enables debug mode for the bot.");
    }

    @Override public void execute(Interaction interaction) {
        // Check if the executor is authorized.
        if(!CommandUtils.isAdministrator(interaction)) return;

        // Get the last state of debug mode.
        var debugMode = DiscordLogger.shouldLog();

        // Toggle debug mode.
        DiscordLogger.shouldLog(!debugMode);
        // Set the channel to this channel.
        DiscordLogger.channel(interaction.getChannel());

        // Reply with a success message.
        interaction.reply(MessageUtils.makeEmbed("Debug mode is now " + (debugMode ? "disabled" : "enabled") + "."), false);
    }
}