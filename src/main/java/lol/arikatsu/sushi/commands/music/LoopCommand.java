package lol.arikatsu.sushi.commands.music;

import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.enums.Loop;
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
public final class LoopCommand extends Command implements Arguments {
    public LoopCommand() {
        super("loop", "Set the loop state of the player.");
    }

    @Override public void execute(Interaction interaction) {
        // Perform audio check.
        var check = CommandUtils.doChannelCheck(interaction); if(!check.success()) return;
        var channel = check.result();

        // Pull arguments.
        var loop = interaction.getArgument("loop", String.class);
        var state = switch(loop) {
            case "Track Loop", "track", "song" -> Loop.CURRENT;
            case "Queue Loop", "queue", "all" -> Loop.QUEUE;
            default -> Loop.NONE;
        };

        // Set loop state and reply.
        MusicManager.getInstance()
            .getTrackManager(channel.getGuild())
            .setLoop(state);
        interaction.reply(MessageUtils.makeEmbed(state.getMessage()), false);
    }

    @Override public Collection<Argument> getArguments() {
        return List.of(
            Argument.createWithChoices("loop", "The loop state to set the player to.", "loop", OptionType.STRING, true, 0,
                "Disable Loop", "Track Loop", "Queue Loop")
        );
    }
}