package lol.arikatsu.sushi.commands.fun;

import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.enums.EmbedType;
import lol.arikatsu.sushi.managers.SnipeManager;
import lol.arikatsu.sushi.objects.CachedMessage;
import lol.arikatsu.sushi.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;

@BotCommand
public final class SnipeCommand extends Command implements Arguments {
    public SnipeCommand() {
        super("snipe", "Snipes the last message in the channel.");
    }

    @Override public void execute(Interaction interaction) {
        // Get arguments.
        var type = interaction.getArgument("type", "deleted", String.class);
        var channel = interaction.getArgument("channel", interaction.getGuild().getTextChannelById(
            interaction.getChannel().getId()
        ), GuildChannel.class);

        // Check if the message is null.
        if(interaction.getMessage() == null) {
            // Reply with a failure message.
            interaction.reply(MessageUtils.makeEmbed("No message found.", EmbedType.ERROR), false); return;
        }

        // Get the sniped message from the channel.
        var message = switch (type) {
            default -> CachedMessage.from(interaction.getMessage());
            case "delete", "deleted" -> SnipeManager.getDeletedMessage(channel);
            case "edit", "edited" -> SnipeManager.getEditedMessage(channel);
        };

        // Check if the message is null.
        if(message == null) {
            // Reply with a failure message.
            interaction.reply(MessageUtils.makeEmbed("No message found.", EmbedType.ERROR), false); return;
        }

        // Send the message.
        interaction.reply(new EmbedBuilder()
            .setColor(EmbedType.SIMPLE.getEmbedColor())
            .setAuthor(message.author().getAsTag(), null, message.author().getAvatarUrl())
            .setDescription(message.messageContent())
            .setTimestamp(message.timestamp())
            .setTitle("Sniped a message!")
            .build(), false);
    }

    @Override public Collection<Argument> getArguments() {
        return List.of(
            Argument.createWithChoices("type", "The type of snipe to perform.", "type", OptionType.STRING, false, 0, "deleted", "edited"),
            Argument.create("channel", "The channel to snipe from.", "channel", OptionType.CHANNEL, false, 1)
        );
    }
}