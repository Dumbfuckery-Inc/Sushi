package lol.arikatsu.sushi.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lol.arikatsu.sushi.Sushi;
import lol.arikatsu.sushi.enums.EmbedType;
import lol.arikatsu.sushi.managers.MusicManager;
import lol.arikatsu.sushi.objects.TrackManager;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import tech.xigam.cch.utils.Interaction;

import java.util.function.Consumer;

public final class MusicUtils {
    private MusicUtils() {
        // This class is not meant to be instantiated.
    }

    /**
     * Plays a track with a specified query.
     * @param query The query to search for.
     * @param interaction The interaction to reply to.
     */
    public static void playTrack(String query, Interaction interaction) {
        MusicUtils.playTrack(query, interaction.getGuild(), interaction);
    }

    /**
     * Plays a track with a specified query.
     * @param query The query to search for.
     * @param interaction The interaction to use.
     */
    public static void playTrack(String query, Guild guild, Interaction interaction) {
        MusicUtils.playTrack(query, guild, interaction.getUser().getId(), result -> {
            if(result instanceof String friendlyError) {
                interaction.reply(MessageUtils.makeEmbed(friendlyError, EmbedType.ERROR), false);
            } else if(result instanceof Exception unfriendlyError) {
                Sushi.getLogger().warn("Unable to play track " + query + ".", unfriendlyError);
                interaction.reply(MessageUtils.makeEmbed("An error occurred while trying to play the track.", EmbedType.ERROR), false);
            } else if(result instanceof AudioTrack track) {
                // Parse track info.
                var title = track.getInfo().title;
                var shortened = title.length() > 60 ? title.substring(0, 60) + "..." : title;
                // Remove disallowed characters.
                shortened = shortened.replace("(", "\\(")
                    .replace(")", "\\)");

                interaction.reply(MessageUtils.makeEmbed("**Queued:** [%s](%s)"
                    .formatted(shortened, track.getInfo().uri)), false);
            } else if(result instanceof AudioPlaylist playlist) {
                // Get the track count.
                var trackCount = playlist.getTracks();

                interaction.reply(MessageUtils.makeEmbed("Queued **%s** tracks from [%s](%s)"
                    .formatted(trackCount, playlist.getName(), query)), false);
            }
        });
    }

    /**
     * Plays a track with a specified query.
     * @param query The query to search for.
     * @param guild The guild to play the track in.
     * @param requester The requester of the track.
     * @param callback The callback to call when the track is loaded.
     */
    public static void playTrack(String query, Guild guild, String requester, Consumer<Object> callback) {
        var trackManager = MusicManager.getInstance().getTrackManager(guild);
        MusicManager.getInstance().getAudioPlayerManager().loadItemOrdered(trackManager,
            query, new ResultHandler(callback, trackManager, requester));
    }

    @AllArgsConstructor
    static class ResultHandler implements AudioLoadResultHandler {
        private final Consumer<Object> callback;
        private final TrackManager trackManager;
        private final String requester;

        @Override public void trackLoaded(AudioTrack track) {
            // Set the track's requester.
            track.setUserData(this.requester);
            // Add the track to the queue.
            this.trackManager.playTrack(track);

            // Call the callback.
            this.callback.accept(track);
        }

        @Override public void playlistLoaded(AudioPlaylist playlist) {
            var tracks = playlist.getTracks();

            // Check if the playlist is too big or empty.
            if(tracks.isEmpty() || tracks.size() > 300) {
                this.callback.accept("Playlist is too big or empty."); return;
            }

            // Check if the playlist is a search result.
            if(playlist.isSearchResult()) {
                // Pull the first result from the search result.
                this.trackLoaded(tracks.get(0));
            } else {
                // Add the tracks to the queue.
                tracks.forEach(track -> track.setUserData(this.requester));
                // Add the tracks to the queue.
                this.trackManager.playTrack(tracks);

                // Call the callback.
                this.callback.accept(playlist);
            }
        }

        @Override public void noMatches() {
            this.callback.accept("No matches found for that search term.");
        }

        @Override public void loadFailed(FriendlyException exception) {
            this.callback.accept(exception);
        }
    }
}