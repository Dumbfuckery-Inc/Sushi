package lol.arikatsu.sushi.commands.administrative;

import lol.arikatsu.sushi.Sushi;
import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.utils.CommandUtils;
import lol.arikatsu.sushi.utils.MessageUtils;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;

@BotCommand
public final class DeployCommand extends Command implements Arguments {
    public DeployCommand() {
        super("deploy", "Deploys the bot's slash-commands.");
    }

    @Override public void execute(Interaction interaction) {
        // Check if the executor is authorized.
        if(!CommandUtils.isAdministrator(interaction)) return;

        // Defer the reply.
        interaction.deferReply();

        // Get the arguments.
        var action = interaction.getArgument("action", String.class);
        var target = interaction.getArgument("target", "guild", String.class);
        // Get the guild.
        var guild = interaction.getGuild();

        // Perform the action.
        if(action.equals("deploy")) {
            // Deploy slash-commands.
            Sushi.getCommandHandler().deployAll(target.equals("guild") ? guild : null);
            // Reply with a success message.
            interaction.reply(MessageUtils.makeEmbed("Deployed all slash-commands" + (target.equals("guild") ? " to " + guild.getName() : "") + "."));
        } else {
            // Purge slash-commands.
            Sushi.getCommandHandler().downsert(target.equals("guild") ? guild : null);
            // Reply with a success message.
            interaction.reply(MessageUtils.makeEmbed("Purged all slash-commands" + (target.equals("guild") ? " from " + guild.getName() : "") + "."));
        }
    }

    @Override public Collection<Argument> getArguments() {
        return List.of(
            Argument.createWithChoices("action", "The action to perform.", "action", OptionType.STRING, true, 0, "deploy", "purge"),
            Argument.createWithChoices("target", "Where should the action be performed?", "target", OptionType.STRING, false, 1, "guild", "global")
        );
    }
}