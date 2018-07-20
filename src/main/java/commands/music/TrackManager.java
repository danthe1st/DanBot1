package commands.music;

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

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import util.STATIC;
/**
 * Core-Klasse f�r Musik
 */
public class TrackManager extends AudioEventAdapter{
	private final AudioPlayer PLAYER;
	private final Queue<AudioInfo> queue;
	
	
	public TrackManager(final AudioPlayer player) {
		this.PLAYER=player;
		this.queue=new LinkedBlockingQueue<>();
	}
	/**
	 * f�gt neuen Track hinzu
	 * @param track der Track
	 * @param author Der, der den Befehl gegeben hat
	 */
	public void queue(final AudioTrack track, final Member author,TextChannel channel) {
		final AudioInfo info=new AudioInfo(track, author,channel);
		queue.add(info);
		
		if (PLAYER.getPlayingTrack()==null) {
			PLAYER.playTrack(track);
		}
	}
	/**
	 * 
	 * @return n�chste Tracks
	 */
	public Set<AudioInfo> getQueue() {
		return new LinkedHashSet<>(queue);
	}
//	public AudioInfo getInfo(final AudioTrack track) {
//		return queue.stream().filter(info -> info.getTrack().equals(track)).findFirst().orElse(null);
//	}
	/**
	 * Queue l�schen
	 */
	public void purgeQueue() {
		queue.clear();
	}
	/**
	 * Reihenfolge zuf�llig ver�ndern
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
	 * Listener: wenn ein Track gestartet wird<br>
	 * 	Wenn Guild-Member der Befehl gesendet hat in VoiceChannel:<br>
	 * 	  spiele Track in diesem Channel ab<br>
	 * 	Wenn nicht:<br>
	 * 		n�chster Track
	 */
	@Override
	public void onTrackStart(final AudioPlayer player, final AudioTrack track) {
		final AudioInfo info=queue.element();
		final VoiceChannel vChan=info.getAuthor().getVoiceState().getChannel();
		if (vChan==null) {
			player.stopTrack();
			
			STATIC.errmsg(info.getTextChannel(), info.getAuthor().getAsMention()+" Cannot play because you are not in a Voice Channel");
		}
		else {
			info.getAuthor().getGuild().getAudioManager().openAudioConnection(vChan);
		}
	}
	/**
	 * Listener: wenn ein Track gestoppt wird<br>
	 * 	spiele n�chstes Element ab oder gehe aus VoiceChannel heraus
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
					new Thread(new ConnectionCloser(g)).start();
					//g.getAudioManager().closeAudioConnection();
			}
		}
		else {
			player.playTrack(queue.element().getTrack());
		}
	}

	
}
