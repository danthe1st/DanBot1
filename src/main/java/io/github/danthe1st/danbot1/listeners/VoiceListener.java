package io.github.danthe1st.danbot1.listeners;

import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import static io.github.danthe1st.danbot1.util.LanguageController.translate;

/**
 * Listener for VoiceLogs
 * @author Daniel Schmid
 */
@BotListener
public class VoiceListener extends ListenerAdapter {
	private static final String VOICE_LOGGER_CHANNEL_NAME="VoiceLog";
	/**
	 * Listener to log when someone joines a {@link VoiceChannel}
	 */
	@Override
	public void onGuildVoiceJoin(final GuildVoiceJoinEvent event) {
		if (event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).isEmpty()||!event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).get(0).canTalk()) {
			return;
		}
		STATIC.msg(event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).get(0), String.format(translate(event.getGuild(),"userJoinedVC"),event.getVoiceState().getMember().getUser().getName(),event.getChannelJoined().getName()));
	}
	/**
	 * Listener to log when someone leaves a {@link VoiceChannel}
	 */
	@Override
	public void onGuildVoiceLeave(final GuildVoiceLeaveEvent event) {
		if (event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).isEmpty()||!event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).get(0).canTalk()) {
			return;
		}
		STATIC.msg(event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).get(0), String.format(translate(event.getGuild(),"userLeftVC"),event.getVoiceState().getMember().getUser().getName(),event.getChannelLeft().getName()));
	}
	/**
	 * Listener to log when someone is moved between {@link VoiceChannel}s
	 */
	@Override
	public void onGuildVoiceMove(final GuildVoiceMoveEvent event) {
		if (event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).isEmpty()||!event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).get(0).canTalk()) {
			return;
		}
		STATIC.msg(event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).get(0), String.format(translate(event.getGuild(),"userVCMove"),event.getVoiceState().getMember().getUser().getName(),event.getChannelLeft().getName(),event.getChannelJoined().getName()));
	}
	/**
	 * Listener to log when someone is muted
	 */
	@Override
	public void onGuildVoiceMute(final GuildVoiceMuteEvent event) {
		if (event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).isEmpty()||!event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).get(0).canTalk()) {
			return;
		}
		if (!event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).isEmpty()) {
			String text;
			if (event.isMuted()) {
				text=String.format(translate(event.getGuild(),"userVCMute"),event.getVoiceState().getMember().getUser().getName(),event.getVoiceState().getChannel().getName() );
			}else {
				text=String.format(translate(event.getGuild(),"userVCUnMute"),event.getVoiceState().getMember().getUser().getName(),event.getVoiceState().getChannel().getName() );
			}
			STATIC.msg(event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).get(0), text);
		}
	}
}
