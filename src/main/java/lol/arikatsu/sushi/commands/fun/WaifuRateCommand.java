package lol.arikatsu.sushi.commands.fun;

import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.utils.CommandUtils;
import lol.arikatsu.sushi.utils.MathUtils;
import lol.arikatsu.sushi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;

import static lol.arikatsu.sushi.objects.Constants.BOT_CONFIG;

@BotCommand
public final class WaifuRateCommand extends Command implements Arguments {
    public WaifuRateCommand() {
        super("waifurate", "Rate yourself or another person as a waifu.");
    }

    @Override public void execute(Interaction interaction) {
        // Check if the command was executed in a guild.
        if(!CommandUtils.inGuild(interaction)) return;

        // Get arguments.
        var user = interaction.getArgument("user", interaction.getMember(), Member.class);

        // Get the name to reply with.
        var name = "You are";
        if(!user.equals(interaction.getMember())) {
            name = user.getUser().getName() + " is";
        }

        // Reply with a rating out of 100.
        if(BOT_CONFIG.administrators.contains(user.getId())) {
            interaction.reply(MessageUtils.makeEmbed(name + " a 100/100 waifu."));
        } else {
            interaction.reply(MessageUtils.makeEmbed(name + " a " + MathUtils.randomNumber(0, 100) + "/100 waifu."));
        }
    }

    @Override public Collection<Argument> getArguments() {
        return List.of(
            Argument.create("user", "The user to rate.", "user", OptionType.USER, false, 0)
        );
    }
}