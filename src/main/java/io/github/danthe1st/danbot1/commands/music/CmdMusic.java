package io.github.danthe1st.danbot1.commands.music;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
/**
 * The music Command
 * @author Daniel Schmid
 */
@BotCommand(aliases = {"m","music"})
public class CmdMusic implements Command{

	private static final AudioPlayerManager MANAGER=new DefaultAudioPlayerManager();
	private static final Map<Guild, Map.Entry<AudioPlayer, TrackManager>> PLAYERS=new HashMap<>();
	private int numTracksToLoad=0;
	private static final int MAX_NUM_TRACKS=100;
	
	public CmdMusic() {
		AudioSourceManagers.registerRemoteSources(MANAGER);
	}
	/**
	 * creates an Audio-Player for a Guild
	 * @param g the {@link Guild}
	 * @return the created Audio Player
	 */
	private AudioPlayer createPlayer(final Guild g) {
		final AudioPlayer p=MANAGER.createPlayer();
		final TrackManager m=new TrackManager(p);
		p.addListener(m);
		g.getAudioManager().setSendingHandler(new PlayerSendHandler(p));
		PLAYERS.put(g, new AbstractMap.SimpleEntry<AudioPlayer, TrackManager>(p, m));
		return p;
	}
	/**
	 * checks if there is an Audio Player for a specified {@link Guild}
	 * @param g the {@link Guild}
	 * @return <code>true</code> if the Audio exists
	 */
	private boolean hasPlayer(final Guild g) {
		return PLAYERS.containsKey(g);
	}
	/**
	 * gets the Audio Player of a {@link Guild}
	 * @param g the {@link Guild}
	 * @return the Audio Player of the {@link Guild}
	 */
	private AudioPlayer getPlayer(final Guild g) {
		if (hasPlayer(g)) {
			return PLAYERS.get(g).getKey();
		}
		else {
			return createPlayer(g);
		}
	}
	/**
	 * gets the {@link TrackManager} for a Guild
	 * @param g the {@link Guild}
	 * @return the {@link TrackManager} of the Guild
	 */
	private TrackManager getManager(final Guild g) {
		return PLAYERS.get(g).getValue();
	}
	/**
	 * tests if music is played
	 * @param g The Guild(Discord-Server)
	 * @return true if music is played
	 */
	private boolean isIdle(final Guild g) {
		return !hasPlayer(g)|| getPlayer(g).getPlayingTrack()==null;
	}
	/**
	 * loads a track into the queue
	 * @param identifier the identifier-String
	 * @param msg The {@link Message} with the Command to load the Music
	 */
	private void loadTrack(final String identifier, final Message msg) {
		Member author=msg.getMember();
		final Guild guild=author.getGuild();
		getPlayer(guild);
		MANAGER.setFrameBufferDuration(1000);
		MANAGER.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
			
			@Override
			public void trackLoaded(final AudioTrack track) {
				getManager(guild).queue(track, author,msg.getTextChannel());
			}
			@Override
			public void playlistLoaded(final AudioPlaylist playlist) {
				//getManager(guild).queue(playlist.getTracks().get(0), author);
				for (int i = 0; i < numTracksToLoad&&i<MAX_NUM_TRACKS; i++) {
					if (playlist.getTracks().size()<i) {
						return;
					}
					getManager(guild).queue(playlist.getTracks().get(i), author,msg.getTextChannel());
				}
			}
			@Override
			public void noMatches() {
				STATIC.errmsg(msg.getTextChannel(), "no tracks found");
			}
			
			@Override
			public void loadFailed(final FriendlyException exception) {
				STATIC.errmsg(msg.getTextChannel(), "Cannot load Track");
			}
		});
		
	}
	/**
	 * skips the active track
	 * @param g The {@link Guild}
	 */
	private void skip(final Guild g) {
		getPlayer(g).stopTrack();
		if (getManager(g).getQueue().isEmpty()) {
			g.getAudioManager().closeAudioConnection();
		}
	}
	/**
	 * gets a time as a String
	 * @param millis The time in Milliseconds
	 * @return The Zeit as String
	 */
	private String getTimeStamp(final long millis) {
		long seconds = millis / 1000;
        final long hours = Math.floorDiv(seconds, 3600);
        seconds = seconds - (hours * 3600);
        final long mins = Math.floorDiv(seconds, 60);
        seconds = seconds - (mins * 60);
        return (hours == 0 ? "" : hours + ":") + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
	}
	/**
	 * gets a String from a {@link AudioInfo}
	 * @param info The Track as {@link AudioInfo}
	 * @return The Track as String
	 */
	private String buildQueueMessage(final AudioInfo info) {
		final AudioTrackInfo trackInfo=info.getTrack().getInfo();
		final String title=trackInfo.title;
		final long length=trackInfo.length;
		return "\'["+getTimeStamp(length)+"]\'"+title+"\n";
	}
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.check(event, "playMusic");
	}
	@Override
	public void action(final String[] args, final MessageReceivedEvent event) {
		Guild guild=event.getGuild();
		if (args.length<1) {
			STATIC.errmsg(event.getTextChannel(), help(STATIC.getPrefixExcaped(guild)));
			return;
		}
		switch (args[0].toLowerCase()) {
		case "play":
		case "p":
			
			if (args.length<2) {
				STATIC.errmsg(event.getTextChannel(), "please Enter a valid source");
				return;
			}
			numTracksToLoad=1;
			if (args.length>2) {
				try {
					numTracksToLoad=Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {}
			}
			String input=Arrays.stream(args).skip(1).map(s->" "+s).collect(Collectors.joining()).substring(1);
			
			if (!(input.startsWith("http://")||input.startsWith("https://"))) {
				input="ytsearch: "+input;
			}
			loadTrack(input, event.getMessage());
			break;
		case "skip":
		case "s":
			if(isIdle(guild)) {
				guild.getAudioManager().closeAudioConnection();
				return;
			}
			for (int i = (args.length>1 ? Integer.parseInt(args[1]) : 1); i==1; i--) {
				skip(guild);
			}
			break;
		case "stop":
			if (isIdle(guild)) {
				return;
			}
			getManager(guild).purgeQueue();
			skip(guild);
			guild.getAudioManager().closeAudioConnection();
			
			break;
		case "shuffle":
			if (isIdle(guild)) {
				getManager(guild).shuffleQueue();
			}
			break;
		case "now":
		case "info":
			if (isIdle(guild)) {
				return;
			}
				final AudioTrack track=getPlayer(guild).getPlayingTrack();
				final AudioTrackInfo info=track.getInfo();
				event.getTextChannel().sendMessage(
						new EmbedBuilder().setDescription("**CURRENT TRACK INFO:**").addField("Title",info.title, false)
						.addField("Duration", "\'["+getTimeStamp(track.getPosition())+"/"+getTimeStamp(track.getDuration())+"]\'", false)
						.addField("Author:", info.author, false)
						.build()
						).queue();
				
		case "queue":
			if (isIdle(guild)) {
				return;
			}
			final int sideNum=args.length>1?Integer.parseInt(args[1]):1;
			final List<String> tracks=new ArrayList<>();
			List<String> trackSublist;
			getManager(guild).getQueue().forEach(audioInfo->tracks.add(buildQueueMessage(audioInfo)));
			if (tracks.size()>20) {
				trackSublist = tracks.subList((sideNum-1)*20, (sideNum-1)*20+20);
			}
			else {
				trackSublist=tracks;
			}
			final String out=trackSublist.stream().collect(Collectors.joining("\n"));
			final int sideNumAll=tracks.size()>=20?tracks.size()/20:1;
			STATIC.msg(event.getTextChannel(), "**CURRENT QUEUE:**\n"+
									"*["+getManager(guild).getQueue().size()+" Tracks | Side "+sideNum+" / "+sideNumAll+"]*"+out);
			
			
			break;
		default:
			STATIC.errmsg(event.getTextChannel(), help(STATIC.getPrefixExcaped(guild)));
			break;
		}
	}

	
	/**
	 * hilfe: gibt Hilfe zu diesem Command als String zur�ck
	 */
	@Override
	public String help(String prefix) {
		return "Play one or more youTube video in the audio channel you are OR\n"
				+ "go to the next music in the Queue OR\n"
				+ "stop the music OR\n"
				+ "shuffle the music OR\n"
				+ "get info about the track you are listening OR\n"
				+ "list the queue\n"
				+ "(see Permission *playMusic* in Command perm get)\n"
				+"*Syntax*: "+prefix+"music play/p (<number of Tracks you want to Play>) <URL of the video>/<search term>, skip/s, stop, shuffle, now/info, queue";
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}
}
