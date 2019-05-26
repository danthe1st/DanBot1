package io.github.danthe1st.danbot1.commands.audio;
/**
 * manages who controls the Audio Connection
 * @author Daniel Schmid
 */

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

/**
 * Controlles which {@link AudioHolder} owns the Audio Handlers of which Guild
 * @author Daniel Schmid
 */
public class AudioHolderController {
	private static Map<Guild, AudioHolder> holders=new HashMap<>();
	
	private AudioHolderController() {}
	
	/**
	 * reserves the handler for an {@link AudioHolder}
	 * @param g the {@link Guild} where it should be reserved
	 * @param holder the {@link AudioHolder} which should use it
	 */
	public static synchronized void reserverHolder(Guild g,AudioHolder holder) {
		AudioHolder currentHolder=holders.get(g);
		if (currentHolder!=null) {
			currentHolder.closeConnection(g);
		}
		holders.put(g, holder);
	}
	/**
	 * Gives an {@link AudioHolder} free<br>
	 * This Method <b>does not</b> call {@link AudioHolder#closeConnection(Guild)}
	 * @param g the Guild where an {@link AudioHolder} should be given free
	 */
	public static synchronized void giveHolderFree(Guild g) {
		holders.remove(g);
	}
	/**
	 * informs all holders that everyone left the {@link VoiceChannel}
	 * @see AudioHolder#onEverybodyLeave(Guild)
	 * @param g the left {@link VoiceChannel}
	 */
	static void informOfLeave(VoiceChannel vc) {
		Guild g=vc.getGuild();
		if (holders.containsKey(g)) {
			holders.get(g).onEverybodyLeave(vc);
		}
	}
}
