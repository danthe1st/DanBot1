package io.github.danthe1st.danbot1.commands.audio.music;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import io.github.danthe1st.danbot1.commands.audio.AudioHolder;
import io.github.danthe1st.danbot1.commands.audio.AudioHolderController;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
/**
 * Core-Class for Music
 */
public class TrackManager extends AudioEventAdapter{
	private final AudioPlayer player;
	private final Queue<AudioInfo> queue;
	private final AudioHolder holder;
	
	public TrackManager(final AudioHolder holder, final AudioPlayer player) {
		this.player=player;
		this.queue=new LinkedBlockingQueue<>();
		this.holder=holder;
	}
	/**
	 * adds a Track
	 * @param track the track to add
	 * @param author The {@link Member} who ordered the track
	 * @param channel the Channel where the Track was requested
	 */
	public void queue(final AudioTrack track, final Member author,TextChannel channel) {
		final AudioInfo info=new AudioInfo(track, author,channel);
		queue.add(info);
		
		if (player.getPlayingTrack()==null) {
			player.playTrack(track);
		}
	}
	/**
	 * gets the next tracks
	 * @return next tracks
	 */
	public Set<AudioInfo> getQueue() {
		return new LinkedHashSet<>(queue);
	}
	/**
	 * deletes the queue
	 */
	public void purgeQueue() {
		queue.clear();
	}
	/**
	 * randomizes the queue
	 */
	public void shuffleQueue() {
		final List<AudioInfo> cqueue = new ArrayList<>(getQueue());
		final AudioInfo current=cqueue.get(0);
		cqueue.remove(0);
		Collections.shuffle(cqueue);
		cqueue.add(current);
		purgeQueue();
		queue.addAll(cqueue);
	}
	/**
	 * listener if a track is started<br>
	 * if the {@link Member} who ordered the track is in a {@link VoiceChannel}:<br>
	 *    play track in the {@link VoiceChannel} of the {@link Member}
	 * else
	 *    next track
	 */
	@Override
	public void onTrackStart(final AudioPlayer player, final AudioTrack track) {
		final AudioInfo info=queue.element();
		final VoiceChannel vChan=info.getAuthor().getVoiceState().getChannel();
		if (vChan==null) {
			player.stopTrack();
			
			STATIC.errmsg(info.getTextChannel(), info.getAuthor().getAsMention()+translate(info.getTextChannel().getGuild(),"errNotInVoiceChannel"));
		}
		else {
			AudioHolderController.reserverHolder(info.getAuthor().getGuild(),holder);
			info.getAuthor().getGuild().getAudioManager().openAudioConnection(vChan);
			
		}
	}
	/**
	 * listener when a track is stopped<br>
	 * play next traxk or leave the {@link VoiceChannel}
	 */
	@Override
	public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason) {
		AudioInfo head=this.queue.poll();
		if (head==null) {
			return;
		}
		final Guild g=head.getAuthor().getGuild();
		
		if (queue.isEmpty()) {
			if (g.getAudioManager().isConnected()) {
					new Thread(()->
						g.getAudioManager().closeAudioConnection()
					
				).start();
			}
		}
		else {
			player.playTrack(queue.element().getTrack());
		}
	}
}
