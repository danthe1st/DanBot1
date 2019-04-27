package io.github.danthe1st.danbot1.commands.audio;
/**
 * manages who controls the Audio Connection
 * @author Daniel Schmid
 */

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.api.entities.Guild;

public class AudioHolderController {
	private static Map<Guild, AudioHolder> holders=new HashMap<>();
	
	private AudioHolderController() {}
	
	public static synchronized void reserverHolder(Guild g,AudioHolder holder) {
		AudioHolder currentHolder=holders.get(g);
		if (currentHolder!=null) {
			currentHolder.closeConnection(g);
		}
		holders.put(g, holder);
	}
	public static synchronized void giveHolderFree(Guild g) {
		holders.remove(g);
	}
	static void informOfLeave(Guild g) {
		if (holders.containsKey(g)) {
			holders.get(g).onEverybodyLeave(g);
		}
	}
}
