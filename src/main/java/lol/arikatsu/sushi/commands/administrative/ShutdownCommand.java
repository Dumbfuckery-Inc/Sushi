package lol.arikatsu.sushi.commands.administrative;

import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.utils.CommandUtils;
import lol.arikatsu.sushi.utils.MessageUtils;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

@BotCommand
public final class ShutdownCommand extends Command {
    public ShutdownCommand() {
        super("shutdown", "Terminates the bot's process.");
    }

    @Override public void execute(Interaction interaction) {
        // Check if the executor is authorized.
        if(!CommandUtils.isAdministrator(interaction)) return;

        // Defer the reply.
        interaction.deferReply();

        // Reply with a success message.
        interaction.reply(MessageUtils.makeEmbed("Shutting down..."));

        // Stop the program.
        new Thread(() -> {
            try { Thread.sleep(2000); }
            catch (InterruptedException ignored) { }

            System.exit(69420);
        }).start();
    }
}