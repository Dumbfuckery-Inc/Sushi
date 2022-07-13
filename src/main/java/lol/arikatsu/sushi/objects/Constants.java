package lol.arikatsu.sushi.objects;

import lol.arikatsu.sushi.Sushi;
import lol.arikatsu.sushi.enums.EmbedType;
import lol.arikatsu.sushi.utils.MessageUtils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Guild.ExplicitContentLevel;
import net.dv8tion.jda.api.entities.Guild.VerificationLevel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Map;

public final class Constants {
    private Constants() {
        // This class is not meant to be instantiated.
    }

    /* Configuration constants. */
    private static final BotConfig CONFIG = Sushi.getConfig();

    public static final BotConfig.Colors COLOR_CONFIG = CONFIG.color;
    public static final BotConfig.Bot BOT_CONFIG = CONFIG.bot;

    /* Response constants. */
    public static final MessageEmbed NO_PERMISSION = MessageUtils.makeEmbed(
        "You do not have permission to use this command.", EmbedType.ERROR
    );

    /* Bot constants. */
    public static final String BOT_VERSION = "1.0.0";
    public static final String JDA_VERSION = "5.0.0-alpha.13";

    /* Info constants. */
    public static final Map<VerificationLevel, String> VERIFICATION_LEVELS = Map.of(
        VerificationLevel.NONE, "None",
        VerificationLevel.LOW, "Low",
        VerificationLevel.MEDIUM, "Medium",
        VerificationLevel.HIGH, "(╯°□°）╯︵ ┻━┻",
        VerificationLevel.VERY_HIGH, "┻━┻ ﾐヽ(ಠ益ಠ)ノ彡┻━┻"
    );

    public static final Map<ExplicitContentLevel, String> EXPLICIT_CONTENT_FILTERS = Map.of(
        ExplicitContentLevel.OFF, "Disabled",
        ExplicitContentLevel.NO_ROLE, "No Role",
        ExplicitContentLevel.ALL, "Everyone"
    );

    public static final Map<OnlineStatus, String> ONLINE_STATUSES = Map.of(
        OnlineStatus.ONLINE, "Online",
        OnlineStatus.IDLE, "Idle",
        OnlineStatus.DO_NOT_DISTURB, "Do Not Disturb",
        OnlineStatus.OFFLINE, "Offline"
    );

    public static final Map<OnlineStatus, String> ONLINE_STATUS_ICONS = Map.of(
        OnlineStatus.ONLINE, "https://emoji.gg/assets/emoji/9166_online.png",
        OnlineStatus.IDLE, "https://emoji.gg/assets/emoji/3929_idle.png",
        OnlineStatus.DO_NOT_DISTURB, "https://emoji.gg/assets/emoji/2531_dnd.png",
        OnlineStatus.OFFLINE, "https://emoji.gg/assets/emoji/7445_status_offline.png"
    );

    public static final Map<ActivityType, String> ACTIVITY_TYPE = Map.of(
        ActivityType.PLAYING, "Playing",
        ActivityType.STREAMING, "Streaming",
        ActivityType.LISTENING, "Listening to",
        ActivityType.WATCHING, "Watching",
        ActivityType.COMPETING, "Competing in"
    );
}