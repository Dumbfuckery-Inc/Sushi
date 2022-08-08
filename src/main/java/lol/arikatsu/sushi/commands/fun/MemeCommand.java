package lol.arikatsu.sushi.commands.fun;

import lol.arikatsu.sushi.Sushi;
import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.enums.EmbedType;
import net.dv8tion.jda.api.EmbedBuilder;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

@BotCommand
public final class MemeCommand extends Command {
    public MemeCommand() {
        super("meme", "Fetch a random meme from Reddit.");
    }

    @Override
    public void execute(Interaction interaction) {
        // Defer the reply.
        interaction.deferReply();

        // Create an embed.
        interaction.reply(Sushi.getRandom().getMeme(new EmbedBuilder()
            .setColor(EmbedType.SIMPLE.getEmbedColor())
            ).build());
    }
}