package io.github.danthe1st.danbot1.commands.audio.record;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import io.github.danthe1st.danbot1.commands.audio.AudioHolder;
import io.github.danthe1st.danbot1.commands.audio.AudioHolderController;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class Recorder implements AudioHolder,AudioReceiveHandler,Closeable{

	private Guild g;
	private static Map<Guild, Recorder> recorders=new HashMap<>(); 
	private static final File REC_DIR=new File(STATIC.getSettingsDir()+"/recordings");
	private List<byte[]> rescievedBytes=new ArrayList<>(1200000);
	private static final double VOLUME=1.0;
	
	static {
		if (!REC_DIR.exists()) {
			try {
				Files.createDirectories(REC_DIR.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				for (Recorder rec : recorders.values()) {
					rec.close();
				}
			}
		}));
	}
	
	private Recorder(Guild g) {
		this.g=g;
	}
	public static Recorder getInstance(Guild g) {
		
		if (recorders.containsKey(g)) {
			return recorders.get(g);
		}
		else {
			Recorder recorder=new Recorder(g);
			recorders.put(g, recorder);
			return recorder;
		}
	}
	private File getNextFile() {
		int i=0;
		File file;
		do {
			file = new File(REC_DIR,"record"+i+".ogg");
			i++;
		}while(file.exists());
		return file;
	}
	public static void removeInstance(Guild g) {
		Recorder instance=recorders.get(g);
		if (instance!=null) {
			instance.close();
			recorders.remove(g);
		}
	}
	@Override
	public void closeConnection(Guild g) {
		g.getAudioManager().closeAudioConnection();
		removeInstance(g);
	}
	@Override
	public void onEverybodyLeave(VoiceChannel vc) {
		closeConnection(vc.getGuild());
		AudioHolderController.giveHolderFree(g);
	}
	@Override
	public boolean canReceiveCombined() {
		return true;
	}
	
	@Override
	public void handleCombinedAudio(CombinedAudio combinedAudio) {
		byte[] decodedData=combinedAudio.getAudioData(VOLUME);
		try {
			rescievedBytes.add(decodedData);
		}catch (OutOfMemoryError e) {
			System.err.println("[Recording] OutOfMemory");
			onEverybodyLeave(g.getMember(g.getJDA().getSelfUser()).getVoiceState().getChannel());
		}
	}
	
	@Override
	public void close(){
		try {
			int size=0;
			for (byte[] bs : rescievedBytes) {
				size+=bs.length;
			}
			byte[] decodedData=new byte[size];
			writeToWAV(getNextFile(), decodedData);
		} catch (IOException|OutOfMemoryError e) {
			System.err.println("[Recording] OutOfMemoryError");
		}
		
	}
	private void writeToWAV(File outFile, byte[] decodedData) throws IOException {
        AudioFormat format = OUTPUT_FORMAT; //new AudioFormat(8000, 16, 1, true, false);
        AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(
                decodedData), format, decodedData.length), AudioFileFormat.Type.WAVE, outFile);
	}
}
