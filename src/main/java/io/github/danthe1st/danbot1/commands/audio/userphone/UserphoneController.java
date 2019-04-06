package io.github.danthe1st.danbot1.commands.audio.userphone;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class UserphoneController{
	private static Map<Guild,AudioHandler> handlers=new HashMap<>();
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				for (Guild g : handlers.keySet()) {
					closeUserphoneConnection(g);
				}
			}
		}));
	}
	public static void openUserphoneConnection(VoiceChannel vc) {
		if (canOpenConnection(vc.getGuild())) {
			vc.getGuild().getAudioManager().openAudioConnection(vc);
			AudioHandler handler=new AudioHandler(vc.getGuild());
			handlers.put(vc.getGuild(),handler);
			vc.getGuild().getAudioManager().setReceivingHandler(handler);
			vc.getGuild().getAudioManager().setSendingHandler(handler);
		}
	}
	public static boolean canOpenConnection(Guild g) {
		return !g.getAudioManager().isConnected();
	}
	public static boolean canCloseConnection(Guild g) {
		return handlers.containsKey(g);
	}
	public static void closeUserphoneConnection(Guild g) {
		g.getAudioManager().closeAudioConnection();
		handlers.remove(g);
	}
	public static void addAudio(Guild g,byte[] data) {
		handlers.forEach((k,v)->{
			if (k!=g) {
				v.addAudio(data);
			}
		});
	}
}
