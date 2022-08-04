package lol.arikatsu.sushi.commands.administrative;

import lol.arikatsu.sushi.Sushi;
import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.enums.EmbedType;
import lol.arikatsu.sushi.utils.CommandUtils;
import lol.arikatsu.sushi.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.command.Completable;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Completion;
import tech.xigam.cch.utils.Interaction;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@BotCommand
public final class AnnouncementCommand extends Command implements Arguments, Completable {
    public AnnouncementCommand() {
        super("announcement", "Sends an announcement to the specified server.");
    }

    @Override public void execute(Interaction interaction) {
        // Check if the executor is authorized.
        if(!CommandUtils.isAdministrator(interaction)) return;

        // Defer the reply.
        interaction.deferReply();

        // Get arguments.
        var server = interaction.getArgument("server", String.class);
        var message = interaction.getArgument("message", String.class);

        // Parse the server.
        var targetGuilds = new LinkedList<Guild>();
        if(MessageUtils.isNumber(server, true)) {
            // Assume the server is a collection of guild IDs.
            var guildIds = server.split("\\.");
            // Loop through the guild IDs.
            for(var guildId : guildIds) {
                // Get the guild.
                var guild = Sushi.getJdaInstance().getGuildById(guildId);
                // Check if the guild exists.
                if(guild == null) {
                    // Reply with an error message.
                    interaction.sendMessage(MessageUtils.makeEmbed("The guild with ID " + guildId + " does not exist.", EmbedType.ERROR));
                } else {
                    // Add the guild to the collection.
                    targetGuilds.add(guild);
                }
            }
        } else {
            // Check if the specified server is 'All Guilds'.
            if(server.equals("All Guilds"))
                // Get all guilds.
                targetGuilds.addAll(Sushi.getJdaInstance().getGuilds());
            else
                // Get the guild.
                targetGuilds.addAll(Sushi.getJdaInstance().getGuildsByName(server, false));
        }

        // Check if the collection is empty.
        if(targetGuilds.isEmpty()) {
            // Reply with an error message.
            interaction.sendMessage(MessageUtils.makeEmbed("No guilds found.", EmbedType.ERROR));
            return;
        }

        // Create the announcement embed.
        var executor = interaction.getMember();
        var embed = new EmbedBuilder()
            .setColor(EmbedType.ANNOUNCEMENT.getEmbedColor())
            .setDescription(message)
            .setFooter("From " + executor.getEffectiveName(), executor.getEffectiveAvatarUrl())
            .setTimestamp(OffsetDateTime.now())
            .build();

        // Send the announcement to all guilds.
        for(var guild : targetGuilds) {
            // Find a suitable channel.
            var fallback = (BaseGuildMessageChannel) guild.getChannels().stream()
                // Find text channels.
                .filter(guildChannel -> guildChannel.getType() == ChannelType.TEXT)
                // Find the first channel.
                .findFirst().orElse(null);
            var channel = (BaseGuildMessageChannel) guild.getChannels().stream()
                // Find news channels.
                .filter(guildChannel -> guildChannel.getType() == ChannelType.NEWS)
                // Find the first channel.
                .findFirst().orElse(fallback);

            // Check if the channel exists.
            if(channel == null) {
                // Reply with an error message.
                interaction.sendMessage(MessageUtils.makeEmbed("No text channel found in " + guild.getName() + ".", EmbedType.ERROR));
                // Remove the guild from the collection.
                targetGuilds.remove(guild); continue;
            }

            // Check if we can write to the channel.
            if(channel.canTalk()) {
                // Send the announcement.
                channel.sendMessageEmbeds(embed).queue();
            } else {
                // Reply with an error message.
                interaction.sendMessage(MessageUtils.makeEmbed("The channel " + channel.getName() + " in " + guild.getName() + " is not writable.", EmbedType.ERROR));
                // Remove the guild from the collection.
                targetGuilds.remove(guild);
            }
        }

        // Reply with a success message.
        interaction.reply(MessageUtils.makeEmbed("Announcement sent to " + targetGuilds.size() + " guilds."), false);
    }

    @Override public void complete(Completion completion) {
        // Get the input from the user.
        var input = completion.getInput();
        // Filter out guilds by name.
        var results = Sushi.getJdaInstance().getGuildCache().stream()
            .filter(guild -> guild.getName().toLowerCase().contains(input.toLowerCase()))
            .toList();

        // Add an option to broadcast to all guilds.
        completion.addChoice("All Guilds", "All Guilds");

        // Add the first four results as choices.
        for(var i = 0; i < 4 && i < results.size(); i++) {
            var guild = results.get(i);
            completion.addChoice(guild.getName(), guild.getId());
        } completion.reply();
    }

    @Override public Collection<Argument> getArguments() {
        return List.of(
            // The server can be an auto-completed guild name, or a manually-pasted guild ID.
            Argument.create("server", "The server to send the message to.", "server", OptionType.STRING, true, 0)
                .completable(true),
            Argument.createTrailingArgument("message", "The message to send.", "message", OptionType.STRING, true, 1)
        );
    }
}