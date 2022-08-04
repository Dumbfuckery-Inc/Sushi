package io.github.ctkwarrior.srod;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@SuppressWarnings("ConstantConditions")
public final class Random {
    @Getter private final OkHttpClient httpClient;
    @Getter private final Gson gson;

    public Random() {
        this(new OkHttpClient(), new Gson());
    }

    /**
     * Gets random advice and sets it as the description.
     * @param embed The embed to set the description of.
     * @return The embed with the description set.
     */
    public EmbedBuilder getAdvice(EmbedBuilder embed) {
        // Make a request to the random advice API.
        var request = new Request.Builder()
            .url("https://api.adviceslip.com/advice")
            .header("User-Agent", UserAgent.getRandomUserAgent())
            .build();
        try (var response = this.httpClient.newCall(request).execute()) {
            var body = response.body().string();
            var parsed = this.gson.fromJson(body, JsonObject.class);
            return embed.setDescription(parsed.get("slip")
                .getAsJsonObject().get("advice").getAsString());
        } catch (IOException ignored) {
            // Return the embed with an error message.
            return embed.setDescription("An error occurred.");
        }
    }

    /**
     * Gets a random fact and sets it as the description.
     * @param embed The embed to set the description of.
     * @return The embed with the description set.
     */
    public EmbedBuilder getFact(EmbedBuilder embed) {
        // Make a request to the random fact API.
        var request = new Request.Builder()
            .url("https://useless-facts.sameerkumar.website/api")
            .header("User-Agent", UserAgent.getRandomUserAgent())
            .build();
        try (var response = this.httpClient.newCall(request).execute()) {
            var body = response.body().string();
            var parsed = this.gson.fromJson(body, JsonObject.class);
            return embed.setDescription(parsed.get("data").getAsString());
        } catch (IOException ignored) {
            // Return the embed with an error message.
            return embed.setDescription("An error occurred.");
        }
    }

    /**
     * Gets a random joke and sets it as the description.
     * @param embed The embed to set the description of.
     * @return The embed with the description set.
     */
    public EmbedBuilder getJoke(EmbedBuilder embed) {
        // Make a request to the random joke API.
        var request = new Request.Builder()
            .url("https://icanhazdadjoke.com/")
            .header("User-Agent", "https://github.com/CTK-WARRIOR/something-random-on-discord")
            .header("Accept", "text/plain")
            .build();
        try (var response = this.httpClient.newCall(request).execute()) {
            var body = response.body().string();
            return embed.setDescription(body);
        } catch (IOException ignored) {
            // Return the embed with an error message.
            return embed.setDescription("An error occurred.");
        }
    }

    /* A list of sub-reddits to pull memes from. */
    private static final List<String> MEME_SUBREDDITS = List.of(
        "memes", "me_irl", "dankmemes", "comedyheaven", "Animemes"
    );

    /**
     * Gets a random meme and sets it as the image.
     * @param embed The embed to set the image of.
     * @return The embed with the image set.
     */
    public EmbedBuilder getMeme(EmbedBuilder embed) {
        // Get a random tag.
        var clone = new LinkedList<>(MEME_SUBREDDITS);
        Collections.shuffle(clone);
        var tag = clone.get(0);

        // Make a request to the random meme API.
        var request = new Request.Builder()
            .url("https://www.reddit.com/r/%s/random/.json".formatted(tag))
            .header("User-Agent", UserAgent.getRandomUserAgent())
            .build();
        try (var response = this.httpClient.newCall(request).execute()) {
            var body = response.body().string();
            var parsed = this.gson.fromJson(body, JsonArray.class);
            var data = parsed.get(0)
                .getAsJsonObject().get("data")
                .getAsJsonObject().get("children")
                .getAsJsonArray().get(0)
                .getAsJsonObject().get("data")
                .getAsJsonObject();
            return embed.setImage(data.get("is_video").getAsBoolean() ?
                "https://freepikpsd.com/wp-content/uploads/2019/10/no-image-png-5-Transparent-Images.png" :
                data.get("url").getAsString());
        } catch (IOException ignored) {
            // Return the embed with an error message.
            return embed.setDescription("An error occurred.");
        }
    }

    /* Valid queries for Random#getAnimeImage(String) */
    private static final List<String> VALID_QUERIES = List.of(
        "pat", "hug", "waifu", "cry", "kiss", "slap", "smug", "punch"
    );

    /**
     * Gets a random anime image.
     * @param query The query to search for.
     * @return The URL of the image.
     */
    public String getAnimeImage(String query) {
        // Check if the query is valid.
        if (!VALID_QUERIES.contains(query)) {
            throw new IllegalArgumentException("Invalid query.");
        }

        // Make a request to the random anime image API.
        var request = new Request.Builder()
            .url("https://neko-love.xyz/api/v1/" + query.toLowerCase())
            .header("User-Agent", UserAgent.getRandomUserAgent())
            .build();
        try (var response = this.httpClient.newCall(request).execute()) {
            var body = response.body().string();
            var parsed = this.gson.fromJson(body, JsonObject.class);
            return parsed.get("url").getAsString();
        } catch (IOException ignored) {
            // Return the Arikatsu API URL.
            return "https://arikatsu.lol";
        }
    }
}