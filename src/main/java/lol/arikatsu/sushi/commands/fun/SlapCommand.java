package lol.arikatsu.sushi.commands.fun;

import lol.arikatsu.sushi.Sushi;
import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.enums.EmbedType;
import lol.arikatsu.sushi.utils.CommandUtils;
import lol.arikatsu.sushi.utils.StringUtils;
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
public final class SlapCommand extends Command implements Arguments {
    public SlapCommand() {
        super("slap", "Slap another user.");
    }

    @Override
    public void execute(Interaction interaction) {
        // Check if the command was executed in a guild.
        if(!CommandUtils.inGuild(interaction)) return;

        // Get arguments.
        var user = interaction.getArgument("user", Member.class);

        // Defer the reply.
        interaction.deferReply();

        // Get a 'slap' anime image URL.
        var slapImage = Sushi.getRandom().getAnimeImage("slap");
        // Check if a reason was provided.
        var reason = interaction.getArgument("reason", "", String.class);
        if(!reason.isBlank()) {
            // Set the slap URL.
            slapImage = "https://vacefron.nl/api/batmanslap?text1=bruh&text2=%s&batman=%s&robin=%s"
                .formatted(reason, StringUtils.encodeUrl(interaction.getUser().getAvatarUrl()),
                    StringUtils.encodeUrl(user.getUser().getAvatarUrl()));
        }

        // Create an embed.
        interaction.reply(new EmbedBuilder()
            .setColor(EmbedType.SIMPLE.getEmbedColor())
            .setFooter(interaction.getUser().getName() + " slaps " + user.getUser().getName())
            .setTimestamp(OffsetDateTime.now())
            .setImage(slapImage)
            .build());
    }

    @Override public Collection<Argument> getArguments() {
        return List.of(
            Argument.create("user", "The user to slap.", "user", OptionType.USER, true, 0),
            Argument.createTrailingArgument("reason", "The reason for the slap.", "reason", OptionType.STRING, false, 1)
        );
    }
}