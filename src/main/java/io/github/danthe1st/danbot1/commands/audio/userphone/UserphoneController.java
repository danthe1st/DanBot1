package io.github.danthe1st.danbot1.commands.audio.userphone;

import java.util.HashMap;
import java.util.Map;

import io.github.danthe1st.danbot1.commands.audio.AudioHolderController;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
/**
 * Core-Class for the userphone connection
 * @author Daniel Schmid
 */
public class UserphoneController{
	private static Map<Guild,AudioHandler> handlers=new HashMap<>();
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(()->{
			for (Guild g : handlers.keySet()) {
				closeConnection(g);
			}
		}));
	}
	
	private UserphoneController() {
		//no instantiation
	}
	
	/**
	 * opens an userphone connection in a Voice Channel
	 * @param vc the {@link VoiceChannel} where the Connection should be opened
	 */
	public static void openUserphoneConnection(VoiceChannel vc) {
		if (canOpenConnection(vc.getGuild())) {
			AudioHandler handler=new AudioHandler(vc.getGuild());
			handlers.put(vc.getGuild(),handler);
			AudioHolderController.reserverHolder(vc.getGuild(), handler);
			vc.getGuild().getAudioManager().openAudioConnection(vc);
			vc.getGuild().getAudioManager().setReceivingHandler(handler);
			vc.getGuild().getAudioManager().setSendingHandler(handler);
		}
	}
	/**
	 * tests if an userphone Connection can be opened in a specified {@link Guild}<br>
	 * An userphone connection can be opened when no connection exists in the same {@link Guild}.
	 * @param g the {@link Guild} (Discord Server)
	 * @return <code>true</code> if a userphone connection can be opened in that guild, else <code>false</code>
	 */
	public static boolean canOpenConnection(Guild g) {
		return !handlers.containsKey(g);
	}
	/**
	 * tests if an userphone Connection can be closed in a specified {@link Guild}<br>
	 * this is the opposite as {@link UserphoneController#canOpenConnection(Guild)}
	 * @param g the {@link Guild} (Discord Server)
	 * @return <code>true</code> if a userphone connection can be closed in that guild, else <code>false</code>
	 */
	public static boolean canCloseConnection(Guild g) {
		return handlers.containsKey(g);
	}
	/**
	 * closes an existing userphone connection in a specific {@link Guild}.
	 * @param g the {@link Guild} (Discord Server)
	 */
	public static void closeConnection(Guild g) {
		g.getAudioManager().closeAudioConnection();
		handlers.remove(g);
	}
	/**
	 * broadcasts audio to all userphone Connections
	 * @param g the {@link Guild} where the audio is from(it shouldn't be sent back)
	 * @param data the audio data as <code>byte[]</code>
	 */
	public static void addAudio(Guild g,byte[] data) {
		handlers.forEach((k,v)->{
			if (k!=g) {
				v.addAudio(data);
			}
		});
	}
}
