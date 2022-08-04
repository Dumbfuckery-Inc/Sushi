package lol.arikatsu.sushi.commands.fun;

import lol.arikatsu.sushi.Sushi;
import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.enums.EmbedType;
import net.dv8tion.jda.api.EmbedBuilder;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

@BotCommand
public final class AdviceCommand extends Command {
    public AdviceCommand() {
        super("advice", "Gives random advice.");
    }

    @Override
    public void execute(Interaction interaction) {
        // Defer the reply.
        interaction.deferReply();

        // Reply with advice.
        interaction.reply(Sushi.getRandom().getAdvice(new EmbedBuilder()
            .setColor(EmbedType.SIMPLE.getEmbedColor())
            ).build(), false);
    }
}