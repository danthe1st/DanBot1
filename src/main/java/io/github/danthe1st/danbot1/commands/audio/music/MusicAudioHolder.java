package io.github.danthe1st.danbot1.commands.audio.music;

import io.github.danthe1st.danbot1.commands.audio.AudioHolder;
import net.dv8tion.jda.api.entities.Guild;

public class MusicAudioHolder implements AudioHolder {
	private CmdMusic cmd;
	private Guild g;
	public MusicAudioHolder(CmdMusic cmd,Guild g) {
		this.cmd=cmd;
		this.g=g;
	}
	@Override
	public void closeConnection() {
		cmd.closeConnection(g);
	}
	@Override
	public void onEverybodyLeave() {
		cmd.onEverybodyLeave(g);
	}
}
