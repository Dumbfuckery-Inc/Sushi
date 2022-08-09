package lol.arikatsu.sushi.objects;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lol.arikatsu.sushi.enums.Loop;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Manages timing of audio tracks.
 */
public final class TrackScheduler extends AudioEventAdapter {
    @Getter private final TrackManager trackManager;
    @Getter private final AudioPlayer audioPlayer;
    @Getter private final BlockingQueue<AudioTrack> queue
        = new LinkedBlockingQueue<>();

    @Getter @Setter private Loop loop = Loop.NONE;

    public TrackScheduler(TrackManager trackManager, AudioPlayer audioPlayer) {
        this.trackManager = trackManager;
        this.audioPlayer = audioPlayer;
    }

    /**
     * Adds a track to the queue or plays it immediately if nothing is playing.
     * @param track The track to play.
     */
    public void playTrack(AudioTrack track) {
        // If the player isn't playing anything, play the track.
        if(this.audioPlayer.getPlayingTrack() == null)
            this.audioPlayer.playTrack(track);

        // Else, add the track to the queue.
        else this.queue.add(track);
    }

    /**
     * Attempts to move to the next track in the queue.
     */
    public void nextTrack() {
        // Check if there is another track in the queue.
        if(this.queue.isEmpty()) {
            this.audioPlayer.stopTrack(); return;
        }

        // Check if the player is playing anything.
        var playingTrack = this.audioPlayer.getPlayingTrack();
        if(playingTrack != null) {
            // Check if the currently playing track should be re-queued.
            if(this.loop == Loop.QUEUE)
                this.queue.add(playingTrack.makeClone());
            // Stop the currently playing track.
            this.audioPlayer.stopTrack();
        }

        // Play the next track in the queue.
        this.audioPlayer.startTrack(this.queue.poll(), false);
    }

    /*
     * Overridden listener methods.
     */

    /**
     * Invoked when the currently playing track ends.
     * @param player Audio player
     * @param track Audio track that ended
     * @param endReason The reason why the track stopped playing
     */
    @Override public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Check if the queue is done.
        if(this.queue.size() == 0 && this.loop == Loop.NONE) {
            this.trackManager.stop(); return;
        }

        // Check if the player should continue playing.
        if(!endReason.mayStartNext) return;

        // Check if the track should be replayed.
        if(this.loop == Loop.CURRENT) {
            this.audioPlayer.startTrack(track.makeClone(), false); return;
        }

        // Check if the track should be re-queued.
        else if (this.loop == Loop.QUEUE)
            this.queue.add(track.makeClone());

        // Play the next track in the queue.
        this.nextTrack();
    }

    /**
     * Invoked when the player encounters an exception while playing a track.
     * @param player Audio player
     * @param track Audio track where the exception occurred
     * @param exception The exception that occurred
     */
    @Override public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // Check if the exception should be ignored and the track should be replayed.
        var cause = exception.getCause();
        if(cause instanceof RuntimeException && cause.getMessage().contains("403")) {
            this.audioPlayer.startTrack(track.makeClone(), false);
        }
    }
}