package listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.STATIC;
/**
 * Listener for autochannel-feature
 * @author Daniel Schmid
 */
public class AutoChannelHandler extends ListenerAdapter{
	List<VoiceChannel> active=new ArrayList<>();
	/**
	 * when someone joines a registrated autochannel the channel will be duplicated with the same properties as the origin channel and the user will be moved into the duplicated Channel
	 */
	@Override
	public void onGuildVoiceJoin(final GuildVoiceJoinEvent event) {
		final HashMap<VoiceChannel, Guild> autoChans=commands.botdata.CmdAutoChannel.getAutoChannels();
		final VoiceChannel vc=event.getChannelJoined();
		final Guild g=event.getGuild();
		if (autoChans.containsKey(vc)) {
			final VoiceChannel nvc=(VoiceChannel)g.getController().createVoiceChannel(vc.getName()+""+STATIC.AUTOCHANNEL_POSTFIX)
					.setBitrate(vc.getBitrate())
					.setUserlimit(vc.getUserLimit())
					.complete();
			if (vc.getParent()!=null) {
				nvc.getManager().setParent(vc.getParent()).queue();
			}
			g.getController().modifyVoiceChannelPositions().selectPosition(nvc).moveUp(1).queue();
			g.getController().moveVoiceMember(event.getMember(), nvc).queue();
			active.add(nvc);
			
		}
	}
	/**
	 * if someone is moved out of a copy of an autochannel and there is no user left the copy will be deleted<br>
	 * if someone is moved into a registrated autochannel the channel will be duplicated with the same properties as the origin channel and the user will be moved into the duplicated Channel
	 */
	@Override
	public void onGuildVoiceMove(final GuildVoiceMoveEvent event) {
		final HashMap<VoiceChannel, Guild> autoChans=commands.botdata.CmdAutoChannel.getAutoChannels();
		final Guild g=event.getGuild();
		
		VoiceChannel vc=event.getChannelJoined();
		if (autoChans.containsKey(vc)) {
			final VoiceChannel nvc=(VoiceChannel)g.getController().createVoiceChannel(vc.getName()+""+STATIC.AUTOCHANNEL_POSTFIX)
					.setBitrate(vc.getBitrate())
					.setUserlimit(vc.getUserLimit())
					.complete();
			if (vc.getParent()!=null) {
				nvc.getManager().setParent(vc.getParent()).queue();
			}
			g.getController().modifyVoiceChannelPositions().selectPosition(nvc).moveUp(1).queue();
			g.getController().moveVoiceMember(event.getMember(), nvc).queue();
			active.add(nvc);
			
		}
		vc=event.getChannelLeft();
		if((active.contains(vc)||isOldAutoChannel(event.getChannelLeft()))&&vc.getMembers().size()==0) {
			active.remove(vc);
			vc.delete().queue();
		}
	}
	/**
	 * if someone leaves a copy of an autochannel the copy will be deleted
	 */
	@Override
	public void onGuildVoiceLeave(final GuildVoiceLeaveEvent event) {
		final VoiceChannel vc=event.getChannelLeft();
		if((active.contains(vc)||isOldAutoChannel(vc))&&vc.getMembers().size()==0) {
			active.remove(vc);
			vc.delete().queue();
		}
	}
	/**
	 * if a registrated autochannel is deleted it will be unregistered
	 */
	@Override
	public void onVoiceChannelDelete(final VoiceChannelDeleteEvent event) {
		final HashMap<VoiceChannel, Guild> autoChans=commands.botdata.CmdAutoChannel.getAutoChannels();
		if (autoChans.containsKey(event.getChannel())) {
			commands.botdata.CmdAutoChannel.unsetChan(event.getChannel());
		}
	}
	/**
	 * tests if a Channel is a copy of an Autochannel created before the Bot started
	 * @param vc The {@link VoiceChannel} to test
	 * @return true if it is a copy of an autochannel
	 */
	private boolean isOldAutoChannel(final VoiceChannel vc) {
		try {
			final HashMap<VoiceChannel, Guild> channels=commands.botdata.CmdAutoChannel.getAutoChannels();
			final String name=vc.getName().substring(0, vc.getName().indexOf(STATIC.AUTOCHANNEL_POSTFIX));
			for (final VoiceChannel voiceChannel : channels.keySet()) {
				if(voiceChannel.getName().equals(name)) {
					return true;
				}
			}
		} catch (final Exception e) {	}
		
		return false;
	}

}
