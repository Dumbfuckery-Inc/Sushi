package lol.arikatsu.sushi.commands.info;

import lol.arikatsu.sushi.Sushi;
import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.enums.EmbedType;
import lol.arikatsu.sushi.objects.Constants;
import lol.arikatsu.sushi.utils.CommandUtils;
import lol.arikatsu.sushi.utils.MathUtils;
import lol.arikatsu.sushi.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.lang.management.ManagementFactory;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@BotCommand
public final class InfoCommand extends Command implements Arguments {
    public InfoCommand() {
        super("info", "Shows information about the bot.");
    }

    @Override public void execute(Interaction interaction) {
        // Send the embed.
        interaction.reply(switch(interaction.getArgument("type", "bot", String.class)) {
            default -> MessageUtils.makeEmbed("Unknown type.", EmbedType.ERROR);
            case "bot" -> InfoCommand.botInfo();
            case "guild" -> InfoCommand.guildInfo(interaction.getGuild());
            case "user" -> InfoCommand.userInfo(interaction.getArgument("user", interaction.getMember(), Member.class));
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
            .setDescription("**Up-Time**: " + MathUtils.formatStopwatch(ManagementFactory.getRuntimeMXBean().getUptime()))

            .addField("Version", Constants.BOT_VERSION, true)
            .addField("JDA Version", Constants.JDA_VERSION, true)
            .addField("Commands", CommandUtils.getCommandCount() + " commands", true)

            .addField("Guilds", guilds.size() + " guilds", true)
            .addField("Users", usersInGuilds + " users", true)
            .addField("Ping", Sushi.getJdaInstance().getGatewayPing() + "ms", true)
            .build();
    }

    private static MessageEmbed guildInfo(Guild guild) {
        // Get information.
        var roleCount = guild.getRoles().size();
        var emojiCount = guild.getEmojis().size();
        var memberCount = guild.getMemberCount();
        var channelCount = guild.getTextChannels().size();
        var botCount = guild.getMembers().stream()
            .filter(member -> member.getUser().isBot()).toList().size();
        var owner = guild.getOwner(); assert owner != null;

        var timeCreated = "<t:" + guild.getTimeCreated().toEpochSecond() + ":F>";

        // Create the description.
        String description = "**• Owner:** " + owner.getAsMention() + "\n" +
            "**• Created At:** " + timeCreated + "\n" +
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

    @SuppressWarnings("ConstantConditions")
    private static MessageEmbed userInfo(Member member) {
        // Get information.
        var user = member.getUser();
        var profile = user.retrieveProfile().complete();
        var status = member.getOnlineStatus();
        var activities = member.getActivities();
        var badges = user.getFlags().stream()
            .map(User.UserFlag::getName)
            .collect(Collectors.joining(", "));

        // Set chaotic properties.
        var description = new LinkedList<String>();
        var thumbnail = new AtomicReference<>(user.getAvatarUrl());

        // Check if any activity is Spotify.
        activities.stream()
            .filter(activity -> activity.getType() == Activity.ActivityType.LISTENING &&
                activity.getName().equals("Spotify"))
            .findFirst().ifPresent(activity -> {
                // Get the activity's rich presence.
                var richPresence = activity.asRichPresence();
                assert richPresence != null;

                // Set chaotic properties.
                thumbnail.set(richPresence.getLargeImage().getUrl());
            });

        // For-each activities.
        for(var activity : activities) {
            // Get the activity's rich presence.
            var richPresence = activity.asRichPresence();
            if(richPresence != null) {
                description.add("**%s**: `%s: %s; %s`".formatted(
                    Constants.ACTIVITY_TYPE.get(richPresence.getType()),
                    activity.getName(), richPresence.getDetails(),richPresence.getState()
                ));
            }
        }

        // Create the embed.
        var embed = new EmbedBuilder()
            .setColor(profile.getAccentColor())
            .setThumbnail(thumbnail.get())
            .setTimestamp(OffsetDateTime.now())
            .setFooter(Constants.ONLINE_STATUSES.get(status), Constants.ONLINE_STATUS_ICONS.get(status))
            .setAuthor(user.getAsTag(), null, user.getAvatarUrl())
            .setDescription(String.join("\n", description))

            .addField("Account Creation", "<t:" + user.getTimeCreated().toEpochSecond() + ":F>", false)
            .addField("Badges", badges, false)

            .addField("User ID", user.getId(), true)
            .addField("Discriminator", user.getDiscriminator(), true)
            .addField("Is Bot?", user.isBot() ? "Yes" : "No", true);

        // Check if the member has a nickname.
        if(member.getNickname() != null)
            embed.addField("Nickname", member.getNickname(), true);

        return embed.build();
    }

    @Override public Collection<Argument> getArguments() {
        return List.of(
            Argument.createWithChoices("type", "The type of into to fetch.", "type", OptionType.STRING, false, 0, "bot", "guild", "user"),
            Argument.create("user", "The user to fetch information about.", "user", OptionType.USER, false, 1)
        );
    }
}