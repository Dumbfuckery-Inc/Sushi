package lol.arikatsu.sushi.objects;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lol.arikatsu.sushi.enums.Loop;
import lol.arikatsu.sushi.managers.MusicManager;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Collection;

/**
 * Manages scheduling & audio playback for a guild.
 */
@Getter public final class TrackManager {
    private final AudioManager audioManager;
    private final AudioPlayer audioPlayer;
    private final TrackScheduler scheduler;

    public TrackManager(Guild guild) {
        this.audioPlayer = MusicManager.getInstance().createAudioPlayer();
        this.scheduler = new TrackScheduler(this, this.audioPlayer);
        this.audioManager = guild.getAudioManager();

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
        this.scheduler.playTrack(track);
    }

    /**
     * Plays all audio tracks in the collection.
     * @param tracks A collection of audio tracks.
     */
    public void playTrack(Collection<AudioTrack> tracks) {
        tracks.forEach(this::playTrack);
    }

    /**
     * Sets the audio player's volume.
     * @param volume An integer between 0-200.
     */
    public void setVolume(float volume) {
        this.audioPlayer.setVolume((int) Math.max(0,
            Math.min(volume, 200)));
    }

    /**
     * Set the loop state for the scheduler.
     * @param loop The loop state.
     */
    public void setLoop(Loop loop) {
        this.scheduler.setLoop(loop);
    }

    /**
     * Stops the audio player and resets it.
     */
    public void stop() {
        this.setVolume(100); // Reset the volume to default.
        this.audioPlayer.destroy(); // Destroy the audio player.

        this.scheduler.getQueue().clear(); // Clear the queue.
        this.setLoop(Loop.NONE); // Disable loop mode.

        if(this.audioManager.isConnected()) // Disconnect from the voice channel if connected.
            this.audioManager.closeAudioConnection(); // Disconnect from the voice channel.
    }
}