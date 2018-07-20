package commands.music;

import net.dv8tion.jda.core.entities.Guild;

public class ConnectionCloser implements Runnable{
	
	Guild g;
	public ConnectionCloser(Guild g) {
		this.g=g;
	}
	
	@Override
	public void run() {
		g.getAudioManager().closeAudioConnection();
	}

}
