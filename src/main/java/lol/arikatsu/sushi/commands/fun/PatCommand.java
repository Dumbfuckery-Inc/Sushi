package lol.arikatsu.sushi.commands.fun;

import lol.arikatsu.sushi.Sushi;
import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.enums.EmbedType;
import lol.arikatsu.sushi.utils.CommandUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@BotCommand
public final class PatCommand extends Command implements Arguments {
    public PatCommand() {
        super("pat", "Pat another user.");
    }

    @Override
    public void execute(Interaction interaction) {
        // Check if the command was executed in a guild.
        if(!CommandUtils.inGuild(interaction)) return;

        // Get arguments.
        var user = interaction.getArgument("user", Member.class);

        // Defer the reply.
        interaction.deferReply();

        // Get a 'pat' anime image URL.
        var patImage = Sushi.getRandom().getAnimeImage("pat");
        // Create an embed.
        interaction.reply(new EmbedBuilder()
            .setColor(EmbedType.SIMPLE.getEmbedColor())
            .setFooter(interaction.getUser().getName() + " pats " + user.getUser().getName())
            .setTimestamp(OffsetDateTime.now())
            .setImage(patImage)
            .build());
    }

    @Override public Collection<Argument> getArguments() {
        return List.of(
            Argument.create("user", "The user to pat.", "user", OptionType.USER, true, 0)
        );
    }
}