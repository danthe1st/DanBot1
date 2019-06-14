package io.github.danthe1st.danbot1.commands.audio.music;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

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
import io.github.danthe1st.danbot1.commands.audio.AudioHolder;
import io.github.danthe1st.danbot1.commands.audio.AudioHolderController;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
/**
 * The music Command
 * @author Daniel Schmid
 */
@BotCommand(aliases = {"m","music"})
public class CmdMusic implements Command{

	private static final AudioPlayerManager MANAGER=new DefaultAudioPlayerManager();
	private static final Map<Guild, Map.Entry<AudioPlayer, TrackManager>> PLAYERS=new HashMap<>();
	private static final Map<Guild, AudioHolder> holders=new HashMap<>();
	private static Map<Guild, PlayerSendHandler> sendHandlers=new HashMap<>();
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
		final TrackManager m=new TrackManager(holders.get(g),p);
		p.addListener(m);
		PlayerSendHandler sendHandler=new PlayerSendHandler(p);
		g.getAudioManager().setSendingHandler(sendHandler);
		sendHandlers.put(g, sendHandler);
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
	private void loadTrack(final String identifier, final Message msg,final int numTracksToLoad) {
		Member author=msg.getMember();
		final Guild guild=author.getGuild();
		getPlayer(guild);
		MANAGER.setFrameBufferDuration(1000);
		guild.getAudioManager().setSendingHandler(sendHandlers.get(guild));
		MANAGER.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
			
			@Override
			public void trackLoaded(final AudioTrack track) {
				getManager(guild).queue(track, author,msg.getTextChannel());
			}
			@Override
			public void playlistLoaded(final AudioPlaylist playlist) {
				for (int i = 0; i < numTracksToLoad&&i<MAX_NUM_TRACKS; i++) {
					if (playlist.getTracks().size()<i) {
						return;
					}
					getManager(guild).queue(playlist.getTracks().get(i), author,msg.getTextChannel());
				}
			}
			@Override
			public void noMatches() {
				STATIC.errmsg(msg.getTextChannel(), translate(msg.getGuild(),"trackNotFound"));
			}
			
			@Override
			public void loadFailed(final FriendlyException exception) {
				STATIC.errmsg(msg.getTextChannel(), translate(msg.getGuild(),"unloadable"));
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
			closeConnection(g);
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
			STATIC.errmsg(event.getTextChannel(), help().replace("--",STATIC.getPrefixEscaped(event.getGuild())));
			return;
		}
		switch (args[0].toLowerCase()) {
		case "play":
		case "p":
			
			if (args.length<2) {
				STATIC.errmsg(event.getTextChannel(), translate(guild,"noQueryString"));
				return;
			}
			int numTracksToLoad=1;
			if (args.length>2) {
				try {
					numTracksToLoad=Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {}
			}
			String input=Arrays.stream(args).skip(1).map(s->" "+s).collect(Collectors.joining()).substring(1);
			
			if (!(input.startsWith("http://")||input.startsWith("https://"))) {
				input="ytsearch: "+input;
			}
			loadTrack(input, event.getMessage(),numTracksToLoad);
			break;
		case "skip":
		case "s":
			if(isIdle(guild)) {
				closeConnection(guild);
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
			closeConnection(guild);
			
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
						new EmbedBuilder().setDescription(translate(guild,"trackInfoTitle")).addField(translate(guild,"Title"),info.title, false)
						.addField(translate(guild,"trackInfoField.duration"), "\'["+getTimeStamp(track.getPosition())+"/"+getTimeStamp(track.getDuration())+"]\'", false)
						.addField(translate(guild,"trackInfoField.author"), info.author, false)
						.build()
						).queue();
				break;
		case "queue":
			if (isIdle(guild)) {
				return;
			}
			final int pageNum=args.length>1?Integer.parseInt(args[1]):1;
			final List<String> tracks=new ArrayList<>();
			List<String> trackSublist;
			getManager(guild).getQueue().forEach(audioInfo->tracks.add(buildQueueMessage(audioInfo)));
			if (tracks.size()>20) {
				trackSublist = tracks.subList((pageNum-1)*20, (pageNum-1)*20+20);
			}
			else {
				trackSublist=tracks;
			}
			final String out=trackSublist.stream().collect(Collectors.joining("\n"));
			final int pageNumAll=tracks.size()>=20?tracks.size()/20:1;
			STATIC.msg(event.getTextChannel(), translate(guild,"trackQueueInfoTitle")+
									"*["+getManager(guild).getQueue().size()+translate(guild,"tracksAndPage")+pageNum+" / "+pageNumAll+"]*"+out);
			break;
		default:
			STATIC.errmsg(event.getTextChannel(), help().replace("--",STATIC.getPrefixEscaped(event.getGuild())));
			break;
		}
	}
	@Override
	public String help() {
		return "musicHelp";
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}
	public void closeConnection(Guild g) {
		g.getAudioManager().closeAudioConnection();
	}
	public void onEverybodyLeave(Guild g) {
		closeConnection(g);
		AudioHolderController.giveHolderFree(g);
	}
}
