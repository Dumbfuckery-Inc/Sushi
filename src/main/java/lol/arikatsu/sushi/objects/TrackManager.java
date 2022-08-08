package lol.arikatsu.sushi.objects;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lol.arikatsu.sushi.enums.Loop;
import lol.arikatsu.sushi.managers.MusicManager;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Collection;

/**
 * Manages scheduling & audio playback for a guild.
 */
@Getter public final class TrackManager {
    private final AudioPlayer audioPlayer;
    private final TrackScheduler scheduler;

    public TrackManager(Guild guild) {
        this.audioPlayer = MusicManager.getInstance().createAudioPlayer();
        this.scheduler = new TrackScheduler(this.audioPlayer);

        this.audioPlayer.addListener(this.scheduler);
    }

    /**
     * Creates a new audio send handler for the player.
     * @return A new audio send handler instance.
     */
    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(this.audioPlayer);
    }

    /**
     * Plays the audio track.
     * @param track An audio track.
     */
    public void playTrack(AudioTrack track) {
        this.audioPlayer.playTrack(track);
    }

    /**
     * Plays all audio tracks in the collection.
     * @param tracks A collection of audio tracks.
     */
    public void playTrack(Collection<AudioTrack> tracks) {
        tracks.forEach(this::playTrack);
    }

    /**
     * Stops the audio player and resets it.
     */
    public void stop() {
        this.audioPlayer.setVolume(100); // Reset the volume to default.
        this.audioPlayer.destroy(); // Destroy the audio player.

        this.scheduler.getQueue().clear(); // Clear the queue.
        this.scheduler.setLoop(Loop.NONE); // Disable loop mode.
    }
}