package lol.arikatsu.sushi.objects;

import lol.arikatsu.sushi.Sushi;

import java.nio.charset.StandardCharsets;
import java.util.List;

public final class BotConfig {
    /* Configuration for the bot. */
    public Bot bot = new Bot();
    /* Configuration for colors. */
    public Colors color = new Colors();
    /* Configuration for networking. */
    public Networking networking = new Networking();

    public static class Bot {
        /* The command prefix. */
        public String prefix = "s-";
        /* The authorized administrators for the bot. */
        public List<String> administrators = List.of("593787701409611776");
    }

    public static class Colors {
        /* This is the embed color used when making an 'info' embed. */
        public String defaultEmbedColor = "#c92c80";
        /* This is the embed color used when making an 'error' embed. */
        public String errorEmbedColor = "#f54040";
        /* This is the embed color used when making an 'announcement' embed. */
        public String announcementEmbedColor = "#49b0f5";
    }

    public static class Networking {
        /* This is the IPv6 block the machine is assigned to. */
        public String ipv6Block = "";
    }

    /**
     * Serializes the configuration to a string.
     * @return The serialized configuration.
     */
    public byte[] serialize() {
        // Serialize the configuration.
        return Sushi.getGson().toJson(this)
            .getBytes(StandardCharsets.UTF_8);
    }
}