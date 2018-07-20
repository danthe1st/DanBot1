package listeners;

import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMuteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.STATIC;
/**
 * Listener f. VoiceLogs
 * @author Daniel Schmid
 *
 */
public class VoiceListener extends ListenerAdapter {
	private static final String VOICE_LOGGER_CHANNEL_NAME="VoiceLog";
	
	
	/**
	 * Wenn jmd in einem Sprachkanal joined und es einen Textkanal VoiceLog gibt wird die aktion in den Textkanal gesendet
	 */
	@Override
	public void onGuildVoiceJoin(final GuildVoiceJoinEvent event) {
		if (event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).isEmpty()||!event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).get(0).canTalk()) {
			return;
		}
		final Message msg=event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).get(0).sendMessage(
			"Member \"" + event.getVoiceState().getMember().getUser().getName()+"\" joined Voice Channel \""+event.getChannelJoined().getName()+"\""
		).complete();
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				msg.delete().queue();
				
			}
		}, STATIC.INFO_TIMEOUT);
	}
	/**
	 * Wenn jmd einen Sprachkanal verlässt und es einen Textkanal VoiceLog gibt wird die aktion in den Textkanal gesendet
	 */
	@Override
	public void onGuildVoiceLeave(final GuildVoiceLeaveEvent event) {
		if (event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).isEmpty()||!event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).get(0).canTalk()) {
			return;
		}
		event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).get(0).sendMessage(
				"Member \"" + event.getVoiceState().getMember().getUser().getName()+"\" left Voice Channel \""+event.getChannelLeft().getName()+"\""
			).queue();
	}
	/**
	 * Wenn jmd zwischen Sprachkanäle gemoved wird und es einen Textkanal VoiceLog gibt wird die aktion in den Textkanal gesendet
	 */
	@Override
	public void onGuildVoiceMove(final GuildVoiceMoveEvent event) {
		if (event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).isEmpty()||!event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).get(0).canTalk()) {
			return;
		}
		event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).get(0).sendMessage(
				"Member \"" + event.getVoiceState().getMember().getUser().getName()+"\" was moved from Voice Channel \""+event.getChannelLeft().getName() +"\" to \""+event.getChannelJoined().getName()+"\""
			).queue();
	}
	/**
	 * Wenn jmd in einem Sprachkanal gemutet wird und es einen Textkanal VoiceLog gibt wird die aktion in den Textkanal gesendet
	 */
	@Override
	public void onGuildVoiceMute(final GuildVoiceMuteEvent event) {
		if (event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).isEmpty()||!event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).get(0).canTalk()) {
			return;
		}
		if (!event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).isEmpty()) {
			String unmuted="";
			if(!event.isMuted()) {
				unmuted="un";
			}
			event.getGuild().getTextChannelsByName(VOICE_LOGGER_CHANNEL_NAME, true).get(0).sendMessage(
					"Member \"" + event.getVoiceState().getMember().getUser().getName()+"\" was "+unmuted+"muted in Voice Channel "+event.getVoiceState().getAudioChannel()
				).queue();
		}
		
	}
}
