package lol.arikatsu.sushi.managers;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Member;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MemberManager {
    /* A map of user ID -> voice channel. */
    private static final Map<String, AudioChannel> channelMap
        = new ConcurrentHashMap<>();

    /**
     * Caches the voice channel of a member.
     * @param member The member that joined a voice channel.
     * @param channel The voice channel the member joined.
     */
    public static void joinChannel(Member member, AudioChannel channel) {
        channelMap.put(member.getId(), channel);
    }

    /**
     * Un-caches the voice channel of a member.
     * @param member The member that left a voice channel.
     */
    public static void leaveChannel(Member member) {
        channelMap.remove(member.getId());
    }

    /**
     * Gets the voice channel for the given user.
     * @param userId The user ID.
     * @return The voice channel.
     */
    public static AudioChannel getChannel(String userId) {
        return channelMap.get(userId);
    }

    /**
     * Checks if the given user's voice state is cached.
     * @param userId The user ID.
     * @return True if the user's voice state is cached.
     */
    public static boolean isCached(String userId) {
        if(!channelMap.containsKey(userId))
            return false;

        return channelMap.get(userId) != null;
    }
}