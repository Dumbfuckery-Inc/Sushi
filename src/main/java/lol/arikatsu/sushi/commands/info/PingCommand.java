package lol.arikatsu.sushi.commands.info;

import lol.arikatsu.sushi.Sushi;
import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.enums.EmbedType;
import net.dv8tion.jda.api.EmbedBuilder;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

@BotCommand
public final class PingCommand extends Command {
    public PingCommand() {
        super("ping", "Returns the bot's ping.");
    }

    @Override public void execute(Interaction interaction) {
        // Defer the reply.
        interaction.deferReply();

        // Get the JDA instance.
        var jdaInstance = Sushi.getJdaInstance();
        // Get the ping to the Discord API.
        var apiPing = jdaInstance.getRestPing().complete();
        // Get the ping to the websocket.
        var websocketPing = jdaInstance.getGatewayPing();

        // Reply with the ping.
        var embed = new EmbedBuilder()
            .setDescription("Pong!")
            .addField("API Ping", apiPing + "ms", true)
            .addField("Websocket Ping", websocketPing + "ms", true)
            .setColor(EmbedType.SIMPLE.getEmbedColor())
            .setFooter("Requested by " + interaction.getMember().getEffectiveName(), interaction.getMember().getEffectiveAvatarUrl())
            .build();
        interaction.reply(embed, false);
    }
}