package lol.arikatsu.sushi.listeners;

import lol.arikatsu.sushi.annotations.BotListener;
import lol.arikatsu.sushi.managers.MemberManager;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@BotListener
public final class VoiceActivityListener extends ListenerAdapter {
    @Override public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        // Cache the member's voice channel.
        MemberManager.joinChannel(event.getMember(), event.getChannelJoined());
    }

    @Override public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        // Un-cache the member's voice channel.
        MemberManager.leaveChannel(event.getMember());
    }

    @Override public void onReady(@NotNull ReadyEvent event) {
        // Cache all connected users.
        event.getJDA().getGuilds().forEach(guild -> guild.getMembers().stream()
            .filter(member -> member.getVoiceState().inAudioChannel())
            .forEach(member -> MemberManager.joinChannel(member, member.getVoiceState().getChannel())));
    }
}