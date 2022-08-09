package lol.arikatsu.sushi.utils;

import lol.arikatsu.sushi.Sushi;
import lol.arikatsu.sushi.managers.MemberManager;
import lol.arikatsu.sushi.objects.Check;
import lol.arikatsu.sushi.objects.Constants;
import net.dv8tion.jda.api.entities.AudioChannel;
import tech.xigam.cch.utils.Interaction;

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
            interaction.reply(Constants.NO_PERMISSION); return false;
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
            // Reply with an error message.
            interaction.reply(Constants.GUILD_ONLY); return false;
        } return true; // Return true if the interaction is in a guild.
    }

    /**
     * Attempts to fetch an audio channel from an interaction.
     * @param interaction The interaction to check.
     * @return The audio channel, or null if none was found.
     */
    public static AudioChannel getChannel(Interaction interaction) {
        // Check if the interaction is in a guild.
        var guild = interaction.getGuild();
        if(guild == null) {
            // Check if user is cached.
            var user = interaction.getUser().getId();
            if(!MemberManager.isCached(user)) {
                // Reply with an error message.
                interaction.reply(Constants.NOT_IN_VOICE_CHANNEL); return null;
            }

            return MemberManager.getChannel(user);
        } else {
            // Get the member's currently active voice channel.
            var voiceState = interaction.getMember().getVoiceState();
            if(voiceState == null || !voiceState.inAudioChannel()) {
                // Reply with an error message.
                interaction.reply(Constants.NOT_IN_VOICE_CHANNEL); return null;
            }

            return voiceState.getChannel();
        }
    }

    /**
     * Checks if the bot is already connected to the voice channel.
     * @param interaction The interaction to check.
     * @return True if the bot is already connected to the voice channel.
     */
    public static Check<AudioChannel> doConnectionCheck(Interaction interaction) {
        var channel = CommandUtils.getChannel(interaction);
        if(channel == null) return new Check<>(false, null);

        var audioManager = channel.getGuild().getAudioManager();
        if(audioManager.isConnected()) {
            // Check if the bot is already connected to the voice channel.
            if(!audioManager.getConnectedChannel().getId().equals(channel.getId())) {
                // Reply with an error message.
                interaction.reply(Constants.ALREADY_CONNECTED);
                return new Check<>(false, null);
            }
        } else {
            // Connect to the voice channel.
            audioManager.openAudioConnection(channel);
        }

        return new Check<>(true, channel);
    }

    /**
     * Checks if the bot is connected to the voice channel.
     * @param interaction The interaction to check.
     * @return True if the bot is connected to the voice channel.
     */
    public static Check<AudioChannel> doChannelCheck(Interaction interaction) {
        var channel = CommandUtils.getChannel(interaction);
        if(channel == null) {
            interaction.reply(Constants.NOT_IN_VOICE_CHANNEL);
            return new Check<>(false, null);
        }

        var audioManager = channel.getGuild().getAudioManager();
        // Check if the bot is in a voice channel.
        if(!audioManager.isConnected()) {
            interaction.reply(Constants.NOT_CONNECTED);
            return new Check<>(false, null);
        }
        // Check if the bot is in the same voice channel as the user.
        if(!audioManager.getConnectedChannel().getId().equals(channel.getId())) {
            interaction.reply(Constants.NOT_IN_VOICE_CHANNEL);
            return new Check<>(false, null);
        } return new Check<>(true, channel);
    }

    /**
     * Gets the total amount of registered commands.
     * @return An integer representing the amount of commands registered.
     */
    public static int getCommandCount() {
        return Sushi.getCommandHandler().getRegisteredCommands().size();
    }
}
