package lol.arikatsu.sushi.commands.music;

import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.managers.MusicManager;
import lol.arikatsu.sushi.utils.CommandUtils;
import lol.arikatsu.sushi.utils.MessageUtils;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

@BotCommand
public final class StopCommand extends Command {
    public StopCommand() {
        super("stop", "Stops the current track.");
    }

    @Override public void execute(Interaction interaction) {
        // Perform audio check.
        var check = CommandUtils.doChannelCheck(interaction); if(!check.success()) return;
        var channel = check.result();

        MusicManager.getInstance()
            .getTrackManager(channel.getGuild())
            .stop();
        interaction.reply(MessageUtils.makeEmbed("The queue has been cleared and the player has been stopped."));
    }
}