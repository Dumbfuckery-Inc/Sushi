package lol.arikatsu.sushi.managers;

import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.getyarn.GetyarnAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.lava.extensions.youtuberotator.YoutubeIpRotatorSetup;
import com.sedmelluq.lava.extensions.youtuberotator.planner.NanoIpRoutePlanner;
import com.sedmelluq.lava.extensions.youtuberotator.tools.ip.Ipv6Block;
import lol.arikatsu.sushi.objects.Constants;
import lol.arikatsu.sushi.objects.TrackManager;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the bot's music functionality.
 */
public final class MusicManager {
    private static MusicManager instance;

    /**
     * Gets the music manager instance.
     * @return The existing music manager instance, or a new one if none exists.
     */
    public static MusicManager getInstance() {
        return instance == null ? instance = new MusicManager() : instance;
    }

    private final Map<String, TrackManager> trackManagers =
        new ConcurrentHashMap<>();
    @Getter private final AudioPlayerManager audioPlayerManager
        = new DefaultAudioPlayerManager();

    @Getter private final YoutubeAudioSourceManager youtubeSource =
        new YoutubeAudioSourceManager();
    @Getter private final SoundCloudAudioSourceManager soundCloudSource =
        SoundCloudAudioSourceManager.createDefault();

    private MusicManager() {
        // Register audio sources.
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
        this.audioPlayerManager.registerSourceManager(this.youtubeSource);
        this.audioPlayerManager.registerSourceManager(this.soundCloudSource);
        this.audioPlayerManager.registerSourceManager(new BandcampAudioSourceManager());
        this.audioPlayerManager.registerSourceManager(new VimeoAudioSourceManager());
        this.audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        this.audioPlayerManager.registerSourceManager(new BeamAudioSourceManager());
        this.audioPlayerManager.registerSourceManager(new GetyarnAudioSourceManager());
        this.audioPlayerManager.registerSourceManager(new HttpAudioSourceManager(MediaContainerRegistry.DEFAULT_REGISTRY));

        // Check if IPv6 request rotation is enabled.
        var ipv6Block = Constants.NETWORK_CONFIG.ipv6Block;
        if(!ipv6Block.isBlank()) {
            new YoutubeIpRotatorSetup(
                new NanoIpRoutePlanner(Collections.singletonList(new Ipv6Block(ipv6Block)), true)
            ).forSource(this.youtubeSource).setup();
        }
    }

    /**
     * Creates an audio player.
     * @return A new audio player.
     */
    public AudioPlayer createAudioPlayer() {
        return this.audioPlayerManager.createPlayer();
    }

    /**
     * Gets the track manager for the specified guild.
     * @param guild The guild to get the track manager for.
     * @return The existing or a new track manager for the guild.
     */
    public TrackManager getTrackManager(Guild guild) {
        return this.trackManagers.computeIfAbsent(guild.getId(), guildId -> {
            var trackManager = new TrackManager(guild);
            guild.getAudioManager().setSendingHandler(trackManager.getSendHandler());
            return trackManager;
        });
    }

    /**
     * Removes the track manager for the specified guild.
     * @param guild The guild to remove the track manager for.
     */
    public void removeTrackManager(Guild guild) {
        this.trackManagers.remove(guild.getId());
    }

    /**
     * Gets all active track managers.
     * @return All active track managers.
     */
    public Collection<TrackManager> getTrackManagers() {
        return this.trackManagers.values();
    }
}