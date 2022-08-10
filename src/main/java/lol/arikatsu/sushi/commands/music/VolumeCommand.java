package lol.arikatsu.sushi.commands.music;

import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.managers.MusicManager;
import lol.arikatsu.sushi.utils.CommandUtils;
import lol.arikatsu.sushi.utils.MessageUtils;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;

@BotCommand
public final class VolumeCommand extends Command implements Arguments {
    public VolumeCommand() {
        super("volume", "Set the volume of the player.");
    }

    @Override public void execute(Interaction interaction) {
        // Perform audio check.
        var check = CommandUtils.doChannelCheck(interaction); if(!check.success()) return;
        var channel = check.result();

        // Pull arguments.
        var volume = interaction.getArgument("volume", Long.class);

        // Set the volume and reply.
        MusicManager.getInstance()
            .getTrackManager(channel.getGuild())
            .setVolume(volume);
        interaction.reply(MessageUtils.makeEmbed("Volume set to **%s**."
            .formatted(volume)), false);
    }

    @Override public Collection<Argument> getArguments() {
        return List.of(
            Argument.create("volume", "The volume to set the player to.", "volume", OptionType.INTEGER, true, 0)
                .range(0, 200)
        );
    }
}