package io.github.danthe1st.danbot1.commands.audio.music;

import io.github.danthe1st.danbot1.commands.audio.AudioHolder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class MusicAudioHolder implements AudioHolder {
	private CmdMusic cmd;
	public MusicAudioHolder(CmdMusic cmd,Guild g) {
		this.cmd=cmd;
	}
	@Override
	public void closeConnection(Guild g) {
		cmd.closeConnection(g);
	}
	@Override
	public void onEverybodyLeave(VoiceChannel vc) {
		cmd.onEverybodyLeave(vc.getGuild());
	}
}
