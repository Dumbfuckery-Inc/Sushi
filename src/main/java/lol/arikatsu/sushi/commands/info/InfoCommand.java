package lol.arikatsu.sushi.commands.info;

import lol.arikatsu.sushi.Sushi;
import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.enums.EmbedType;
import lol.arikatsu.sushi.objects.Constants;
import lol.arikatsu.sushi.utils.CommandUtils;
import lol.arikatsu.sushi.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@BotCommand
public final class InfoCommand extends Command implements Arguments {
    public InfoCommand() {
        super("info", "Shows information about the bot.");
    }

    @Override public void execute(Interaction interaction) {
        // Send the embed.
        interaction.reply(switch(interaction.getArgument("type", String.class)) {
            default -> MessageUtils.makeEmbed("Unknown type.", EmbedType.ERROR);
            case "bot" -> InfoCommand.botInfo();
            case "guild" -> InfoCommand.guildInfo(interaction.getGuild());

        }, false);
    }

    private static MessageEmbed botInfo() {
        // Get information.
            var guilds = Sushi.getJdaInstance().getGuildCache();
        var usersInGuilds = guilds.stream()
            .map(Guild::getMemberCount)
            .reduce(0, Integer::sum);

        // Create the embed.
        return new EmbedBuilder()
            .setColor(EmbedType.SIMPLE.getEmbedColor())
            .setThumbnail(Sushi.getJdaInstance().getSelfUser().getAvatarUrl())
            .setTitle("Sushi's Info")
            .setTimestamp(OffsetDateTime.now())
            .setFooter("Sushi", Sushi.getJdaInstance().getSelfUser().getAvatarUrl())

            .addField("**Version**", Constants.BOT_VERSION, true)
            .addField("**JDA Version**", Constants.JDA_VERSION, true)
            .addField("**Commands**", CommandUtils.getCommandCount() + " commands", true)

            .addField("**Guilds**", guilds.size() + " guilds", true)
            .addField("**Users**", usersInGuilds + " users", true)
            .addField("**Ping**", Sushi.getJdaInstance().getGatewayPing() + "ms", true)
            .build();
    }

    private static MessageEmbed guildInfo(Guild guild) {
        // Get information.
        var roleCount = guild.getRoles().size();
        var emojiCount = guild.getEmojis().size();
        var memberCount = guild.getMemberCount();
        var channelCount = guild.getTextChannels().size();
        var botCount = guild.getMembers().stream()
            .filter(member -> member.getUser().isBot()).toList();
        var owner = guild.getOwner(); assert owner != null;

        // Create the description.
        String description = "**• Owner:** " + owner.getAsMention() + "\n" +
            "**• Created At:** " + guild.getTimeCreated() + "\n" +
            "**• Roles:** " + roleCount + "\n" +
            "**• Emojis:** " + emojiCount + "\n" +
            "**• Boost Count:** " + guild.getBoostCount() + "\n" +
            "**• Verification Level:** " + Constants.VERIFICATION_LEVELS.get(guild.getVerificationLevel()) + "\n" +
            "**• Explicit Content Filter:** " + Constants.EXPLICIT_CONTENT_FILTERS.get(guild.getExplicitContentLevel()) + "\n" +
            "**• Members:** " + memberCount + "\n" +
            "**• Channels:** " + channelCount + "\n" +
            "**• Bots:** " + botCount + "\n";

        // Create the embed.
        return new EmbedBuilder()
            .setColor(EmbedType.SIMPLE.getEmbedColor())
            .setThumbnail(guild.getIconUrl())
            .setTitle("Guild Information for " + guild.getName())
            .setTimestamp(OffsetDateTime.now())
            .setFooter("Sushi", Sushi.getJdaInstance().getSelfUser().getAvatarUrl())
            .setDescription(description)
            .build();
    }

    @Override public Collection<Argument> getArguments() {
        return List.of(
            Argument.createWithChoices("type", "The type of into to fetch.", "type", OptionType.STRING, true, 0, "bot", "guild", "user"),
            Argument.create("user", "The user to fetch information about.", "user", OptionType.USER, false, 1)
        );
    }
}