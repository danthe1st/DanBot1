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
 * Listener f. autochannel-Feature
 * @author Daniel Schmid
 *
 */
public class AutoChannelHandler extends ListenerAdapter{
	List<VoiceChannel> active=new ArrayList<>();
	/**
	 * wenn jmd einem Sprachkanal beitritt:<br>
	 * wenn dieser Sprachkanal ein registrierter, autochannel: erstelle neuen Channel mit selben Eigenschaften und positioniert den beigetretenen User in diesem
	 */
	@Override
	public void onGuildVoiceJoin(final GuildVoiceJoinEvent event) {
		final HashMap<VoiceChannel, Guild> autoChans=commands.utils.CmdAutoChannel.getAutoChannels();
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
	 * venn jmd gemoved wird bzw den Channel wechselt<br>
	 * ist der gewechselte Channel eine autochannel-Kopie und der gewechselte ist letzter: l�sche diese autochannel-Kopie<br>
	 * wird in einem registriertem, autochannel gewechselt: erstelle neuen Channel mit selben Eigenschaften und positioniert den beigetretenen User in diesem
	 */
	@Override
	public void onGuildVoiceMove(final GuildVoiceMoveEvent event) {
		final HashMap<VoiceChannel, Guild> autoChans=commands.utils.CmdAutoChannel.getAutoChannels();
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
	 * wenn jmd. einen Sprachkanal verl�sst<br>
	 * ist der verlassene Channel eine autochannel-Kopie und der User der den Channel verlassen hat ist letzter: l�sche diese autochannel-Kopie<br>
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
	 * wenn ein Sprachkanal gelöscht wird:<br>
	 * wenn dieser ein registrierter autochannel ist: unregistriere diesen
	 */
	@Override
	public void onVoiceChannelDelete(final VoiceChannelDeleteEvent event) {
		final HashMap<VoiceChannel, Guild> autoChans=commands.utils.CmdAutoChannel.getAutoChannels();
		if (autoChans.containsKey(event.getChannel())) {
			commands.utils.CmdAutoChannel.unsetChan(event.getChannel());
		}
	}
	/**
	 * wenn der Sprachkanal ein autochannel ist, der erstellt und nict mehr gel�scht wurde-->true
	 * @param vc Der Discord-Sprachkanal
	 * @return <code>true</code> wenn alter autochannel sonst <code>false</code>
	 */
	private boolean isOldAutoChannel(final VoiceChannel vc) {
		try {
			final HashMap<VoiceChannel, Guild> channels=commands.utils.CmdAutoChannel.getAutoChannels();
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
