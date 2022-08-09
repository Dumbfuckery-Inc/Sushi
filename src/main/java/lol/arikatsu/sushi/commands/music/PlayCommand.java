package lol.arikatsu.sushi.commands.music;

import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.utils.CommandUtils;
import lol.arikatsu.sushi.utils.MessageUtils;
import lol.arikatsu.sushi.utils.MusicUtils;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;

@BotCommand
public final class PlayCommand extends Command implements Arguments {
    public PlayCommand() {
        super("play", "Play a track with a link or query.");
    }

    @Override public void execute(Interaction interaction) {
        // Perform connection check.
        var check = CommandUtils.doConnectionCheck(interaction); if(!check.success()) return;

        // Get arguments.
        var query = interaction.getArgument("query", String.class);
        // Check if the query is a search query.
        if(!MessageUtils.isUrl(query))
            // Prepend the search prefix to the query.
            query = "ytsearch:" + query;

        // Search and play track.
        MusicUtils.playTrack(query, check.result().getGuild(), interaction);
    }

    @Override public Collection<Argument> getArguments() {
        return List.of(
            Argument.createTrailingArgument("query", "The track to play, by URL or query.", "query", OptionType.STRING, true, 0)
        );
    }
}