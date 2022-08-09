package lol.arikatsu.sushi.commands.fun;

import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.enums.EmbedType;
import lol.arikatsu.sushi.utils.MathUtils;
import lol.arikatsu.sushi.utils.MessageUtils;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;

@BotCommand
public final class MathCommand extends Command implements Arguments {
    public MathCommand() {
        super("math", "Evaluates a provided expression.");
    }

    @Override
    public void execute(Interaction interaction) {
        // Get arguments.
        var expression = interaction.getArgument("expression", String.class);

        // Defer the reply.
        interaction.deferReply();

        try {
            // Evaluate the expression.
            var result = MathUtils.evaluate(expression);
            // Reply with the result.
            interaction.reply(MessageUtils.makeEmbed("Result: `" + result + "`"), false);
        } catch (RuntimeException ignored) {
            // If the expression is invalid, reply with an error message.
            interaction.reply(MessageUtils.makeEmbed("Invalid expression.", EmbedType.ERROR), false);
        }
    }

    @Override
    public Collection<Argument> getArguments() {
        return List.of(
            Argument.createTrailingArgument("expression", "The expression to evaluate.", "expression", OptionType.STRING, true, 0)
        );
    }
}