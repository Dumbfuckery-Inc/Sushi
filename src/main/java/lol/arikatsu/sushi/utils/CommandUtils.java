package lol.arikatsu.sushi.utils;

import lol.arikatsu.sushi.Sushi;
import lol.arikatsu.sushi.objects.Constants;
import tech.xigam.cch.ComplexCommandHandler;
import tech.xigam.cch.utils.Interaction;

import java.util.Map;

import static lol.arikatsu.sushi.objects.Constants.BOT_CONFIG;

public final class CommandUtils {
    private CommandUtils() {
        // This class is not meant to be instantiated.
    }

    /**
     * Checks if the interaction comes from a bot administrator.
     * @param interaction The interaction to check.
     * @return True if the interaction comes from a bot administrator.
     */
    public static boolean isAdministrator(Interaction interaction) {
        // Check if the executor is authorized.
        var executor = interaction.getMember();
        if(!BOT_CONFIG.administrators.contains(executor.getId())) {
            // Reply with a permission message.
            interaction.reply(Constants.NO_PERMISSION, false); return false;
        } return true; // Return true if the executor is authorized.
    }

    /**
     * Checks if the interaction was made in a guild.
     * @param interaction The interaction to check.
     * @return True if the interaction was made in a guild.
     */
    public static boolean inGuild(Interaction interaction) {
        // Check if the interaction is in a guild.
        var guild = interaction.getGuild();
        if(guild == null) {
            // Reply with a permission message.
            interaction.reply(Constants.GUILD_ONLY, false); return false;
        } return true; // Return true if the interaction is in a guild.
    }

    /**
     * Gets the total amount of registered commands.
     * @return An integer representing the amount of commands registered.
     */
    public static int getCommandCount() {
        try {
            // Get the command handler class.
            var clazz = ComplexCommandHandler.class;
            // Get the commands field.
            var commands = clazz.getDeclaredField("commands");
            // Make the field accessible.
            commands.setAccessible(true);

            // Get the commands.
            var registered = commands.get(Sushi.getCommandHandler());
            // Return the amount of commands.
            return ((Map<?, ?>) registered).size();
        } catch (Exception ignored) {
            return 69420;
        }
    }
}
