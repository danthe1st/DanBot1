package io.github.danthe1st.danbot1.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
/**
 * Information-Class for an Audio Track<br>
 * contains the author and the track itself.
 * @author Daniel Schmid
 */
public class AudioInfo {

	private final AudioTrack track;
	private final Member author;
	private final TextChannel textChannel;
	
	public AudioInfo(final AudioTrack track, final Member member,final TextChannel channel) {
		this.track=track;
		this.author=member;
		this.textChannel=channel;
	}
	
	public AudioTrack getTrack() {
		return track;
	}

	public Member getAuthor() {
		return author;
	}
	public TextChannel getTextChannel() {
		return textChannel;
	}
}
