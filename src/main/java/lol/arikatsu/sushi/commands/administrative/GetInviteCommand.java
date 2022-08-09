package lol.arikatsu.sushi.commands.administrative;

import lol.arikatsu.sushi.Sushi;
import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.enums.EmbedType;
import lol.arikatsu.sushi.utils.CommandUtils;
import lol.arikatsu.sushi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IInviteContainer;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.command.Completable;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Completion;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BotCommand
public final class GetInviteCommand extends Command implements Arguments, Completable {
    public GetInviteCommand() {
        super("getinvite", "Creates an invite to a guild the bot is in.");
    }

    @Override public void execute(Interaction interaction) {
        // Check if the executor is authorized.
        if(!CommandUtils.isAdministrator(interaction)) return;

        // Defer the reply.
        interaction.deferReply();

        // Get arguments.
        var server = interaction.getArgument("server", String.class);

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

        // Create an invitation for each server.
        var invites = new LinkedList<String>();
        for(var guild : targetGuilds) {
            // Get valid channels.
            var channels = guild.getChannels();
            // Check if the collection is empty.
            if(channels.isEmpty()) {
                // Reply with an error message.
                interaction.sendMessage(MessageUtils.makeEmbed("The guild " + guild.getName() + " has no channels.", EmbedType.ERROR));
            } else {
                // Get the first valid channel.
                var channel = (IInviteContainer) channels.stream()
                    .filter(guildChannel ->
                        guildChannel.getType() == ChannelType.TEXT ||
                        guildChannel.getType() == ChannelType.VOICE)
                    .findFirst().orElse(null);
                // Check if the channel is null.
                if(channel == null) {
                    // Reply with an error message.
                    interaction.sendMessage(MessageUtils.makeEmbed("The guild " + guild.getName() + " has no channels.", EmbedType.ERROR));
                } else {
                    // Create an invitation for the channel.
                    invites.add(channel.createInvite()
                        .setTemporary(true)
                        .setMaxUses(1)
                        .setMaxAge(5L, TimeUnit.MINUTES)
                        .complete().getCode());
                }
            }
        }

        // Reply with the invites.
        if(invites.isEmpty()) {
            // Reply with an error message.
            interaction.reply(MessageUtils.makeEmbed("No invites were created.", EmbedType.ERROR), false);
        } else {
            // Reply with the invites.
            interaction.reply(MessageUtils.makeEmbed("Invites created: " + String.join(", ", invites)), false);
        }
    }

    @Override public void complete(Completion completion) {
        // Get the input from the user.
        var input = completion.getInput();
        // Filter out guilds by name.
        var results = Sushi.getJdaInstance().getGuildCache().stream()
            .filter(guild -> guild.getName().toLowerCase().contains(input.toLowerCase()))
            .toList();

        // Add the first five results as choices.
        for(var i = 0; i < 5 && i < results.size(); i++) {
            var guild = results.get(i);
            completion.addChoice(guild.getName(), guild.getId());
        } completion.reply();
    }

    @Override public Collection<Argument> getArguments() {
        return List.of(
            Argument.create("server", "The server to create an invite for.", "server", OptionType.STRING, true, 0)
                .completable(true)
        );
    }
}