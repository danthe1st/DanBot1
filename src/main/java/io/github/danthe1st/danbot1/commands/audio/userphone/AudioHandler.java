package io.github.danthe1st.danbot1.commands.audio.userphone;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.Guild;

public class AudioHandler implements AudioReceiveHandler, AudioSendHandler {
	private static final double VOLUME = 1;
	private ConcurrentLinkedQueue<byte[]> buffer;
	private Guild g;
	public AudioHandler(Guild g) {
		buffer=new ConcurrentLinkedQueue<>();
		this.g=g;
	}
	
	@Override
	public boolean canProvide() {
		return !buffer.isEmpty();
	}

	@Override
	public ByteBuffer provide20MsAudio() {
		byte[] data=buffer.poll();
		return data!=null?ByteBuffer.wrap(data):null;
	}

	@Override
	public boolean canReceiveCombined() {
		return true;
	}

	@Override
	public boolean canReceiveUser() {
		return false;
	}

	@Override
	public void handleCombinedAudio(CombinedAudio combinedAudio) {
		UserphoneController.addAudio(g,combinedAudio.getAudioData(VOLUME));
	}

	@Override
	public void handleUserAudio(UserAudio userAudio) {

	}
	public void addAudio(byte[] data) {
		buffer.offer(data);
	}
}
