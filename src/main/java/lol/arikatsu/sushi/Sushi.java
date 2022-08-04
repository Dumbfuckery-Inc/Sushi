package lol.arikatsu.sushi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.ctkwarrior.srod.Random;
import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.annotations.BotListener;
import lol.arikatsu.sushi.enums.EmbedType;
import lol.arikatsu.sushi.objects.BotConfig;
import lol.arikatsu.sushi.utils.MessageUtils;
import lol.arikatsu.sushi.utils.ReflectionUtils;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.OkHttpClient;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.xigam.cch.ComplexCommandHandler;
import tech.xigam.cch.command.BaseCommand;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.EnumSet;

import static lol.arikatsu.sushi.objects.Constants.BOT_CONFIG;

public final class Sushi {
    /* The global SLF4J logger instance. */
    @Getter private static final Logger logger =
        LoggerFactory.getLogger(Sushi.class);
    /* The global gson serialization instance. */
    @Getter private static final Gson gson =
        new GsonBuilder().setPrettyPrinting().create();
    /* The global Reflections reflector instance. */
    @Getter private static final Reflections reflector =
        new Reflections("lol.arikatsu.sushi");
    /* The global OkHttp client instance. */
    @Getter private static final OkHttpClient httpClient =
        new OkHttpClient.Builder().build();
    /* The global Random wrapper instance. */
    @Getter private static final Random random
        = new Random(httpClient, gson);
    /* The global bot configuration instance. */
    @Getter private static BotConfig config;

    /* The command handler for the bot. */
    @Getter private static final ComplexCommandHandler commandHandler =
        new ComplexCommandHandler(true);
    /* The JDA instance for the bot. */
    @Getter private static JDA jdaInstance;

    static {
        // Declare the logback configuration file.
        System.setProperty("logback.configurationFile",
            "src/main/resources/logback.xml");
    }

    /**
     * Entrypoint for the Sushi application.
     * @param args The command line arguments.
     *             The first argument is the bot's token.
     *             (optional) The second argument is the path to the bot's configuration file.
     */
    public static void main(String[] args) {
        // Check if enough arguments were supplied.
        if(args.length < 1) {
            logger.error("Not enough arguments supplied.");
            System.exit(1); return;
        }

        // Get the bot's configuration path.
        var configFile = new File(args.length > 1 ? args[1] : "config.json");
        // Check if the configuration file exists.
        if(!configFile.exists()) {
            try {
                // Write a default configuration file.
                Files.write(configFile.toPath(), new BotConfig().serialize());
                // Log a message to the console.
                logger.warn("Created a default configuration file. Please edit before restarting.");
            } catch (IOException ignored) {
                // Log an error to the console.
                logger.error("Failed to create a default configuration file.");
            } System.exit(1); return;
        } else try {
            config = gson.fromJson(new FileReader(configFile, StandardCharsets.UTF_8), BotConfig.class);
        } catch (IOException ignored) {
            // Log an error to the console.
            logger.error("Failed to read the configuration file.");
            System.exit(1); return;
        }

        try {
            // Create a JDA instance.
            jdaInstance = JDABuilder.create(args[0], EnumSet.allOf(GatewayIntent.class))
                // Set the bot's default activity.
                .setActivity(Activity.competing("deez nuts"))
                // Set the bot's cache flags.
                .enableCache(CacheFlag.ACTIVITY, CacheFlag.ONLINE_STATUS, CacheFlag.VOICE_STATE)
                // Set the bot's HTTP client.
                .setHttpClient(httpClient)
                // Build the JDA instance.
                .build();

            commandHandler
                // Set the command prefix.
                .setPrefix(BOT_CONFIG.prefix)
                // Set the command handler's JDA instance.
                .setJda(jdaInstance);

            // Register commands.
            Sushi.registerCommands();
            // Register listeners.
            Sushi.registerListeners();
        } catch (LoginException ignored) {
            // Log a login warning if the bot failed to log in.
            logger.warn("Unable to authenticate with the Discord API. Is your bot token valid?");
        } catch (Exception exception) {
            // Log an error if an exception was thrown during start-up.
            logger.error("An exception was thrown during start-up.", exception);
        }
    }

    /**
     * Registers all commands annotated with {@link BotCommand}.
     */
    private static void registerCommands() throws Exception {
        // Set the argument error handler.
        commandHandler.onArgumentError = interaction ->
            interaction.reply(MessageUtils.makeEmbed("This command requires more arguments!", EmbedType.ERROR), false);

        // Get all classes annotated with BotCommand.
        var commands = ReflectionUtils.getAllOf(BotCommand.class);
        // Register all commands.
        for(var command : commands) {
            // Create an instance of the command.
            var commandInstance = command.getConstructor().newInstance();
            // Check if the command is null or isn't an instance of BaseCommand.
            if(!(commandInstance instanceof BaseCommand)) {
                // Log an error if the command is null or isn't an instance of BaseCommand.
                logger.warn("Command {} is null or isn't an instance of BaseCommand.",
                    command.getSimpleName());
                continue;
            }

            // Register the command.
            commandHandler.registerCommand((BaseCommand) commandInstance);
        }
    }

    /**
     * Registers all listeners annotated with {@link BotListener}.
     */
    private static void registerListeners() throws Exception {
        // Get all classes annotated with BotCommand.
        var listeners = ReflectionUtils.getAllOf(BotListener.class);
        // Register all commands.
        for(var listener : listeners) {
            // Create an instance of the command.
            var listenerInstance = listener.getConstructor().newInstance();
            // Check if the command is null or isn't an instance of BaseCommand.
            if(!(listenerInstance instanceof ListenerAdapter)) {
                // Log an error if the command is null or isn't an instance of BaseCommand.
                logger.warn("Listener {} is null or isn't an instance of listenerInstance.",
                    listener.getSimpleName());
                continue;
            }

            // Register the listener.
            jdaInstance.addEventListener(listenerInstance);
        }
    }
}