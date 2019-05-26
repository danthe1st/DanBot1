package io.github.danthe1st.danbot1.commands.audio;

import io.github.danthe1st.danbot1.listeners.BotListener;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@BotListener
public class VoiceLeaveListener extends ListenerAdapter{
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		VoiceChannel channel=event.getChannelLeft();
		if (channel.getMembers().contains(event.getGuild().getMember(event.getJDA().getSelfUser()))) {
			for (Member member : channel.getMembers()) {
				if (!member.getUser().isBot()) {
					return;
				}
			}
			AudioHolderController.informOfLeave(event.getChannelLeft());
		}
	}
}
