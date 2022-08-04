package lol.arikatsu.sushi.commands.info;

import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.enums.EmbedType;
import lol.arikatsu.sushi.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

@BotCommand
public final class ServerIconCommand extends Command {
    public ServerIconCommand() {
        super("servericon", "Gets the server icon.");
    }

    @Override public void execute(Interaction interaction) {
        // Get information.
        var iconProxy = interaction.getGuild().getIcon();
        // Check if the icon is null.
        if (iconProxy == null) {
            // Send error message.
            interaction.sendMessage(MessageUtils.makeEmbed("The server has no icon.", EmbedType.ERROR));
            return;
        }

        // Get the icon URL.
        var iconUrl = iconProxy.getUrl(1024);

        // Create and send the embed.
        interaction.reply(new EmbedBuilder()
            .setColor(EmbedType.SIMPLE.getEmbedColor())
            .setImage(iconUrl)
            .setDescription("[Download](%s)".formatted(iconUrl))
            .build(), false);
    }
}