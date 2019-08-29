package io.github.danthe1st.danbot1.commands.audio.record;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.sedmelluq.discord.lavaplayer.natives.vorbis.VorbisDecoder;

import io.github.danthe1st.danbot1.commands.audio.AudioHolder;
import io.github.danthe1st.danbot1.commands.audio.AudioHolderController;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class Recorder implements AudioHolder,AudioReceiveHandler,Closeable{

	private Guild g;
	private static Map<Guild, Recorder> recorders=new HashMap<>(); 
	private static final File REC_DIR=new File(STATIC.getSettingsDir()+"/recordings");
	private List<byte[]> rescievedBytes=new ArrayList<>();
	private static final double VOLUME=1.0;
	
	static {
		if (!REC_DIR.exists()) {
			REC_DIR.mkdirs();
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
		nextFile();
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
	public void nextFile() {
		File file=getNextFile();
		try {
			file.createNewFile();
			
			//this.file=new VorbisFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	public boolean canReceiveUser() {
		return false;
	}
	@Override
	public void handleCombinedAudio(CombinedAudio combinedAudio) {
		try {
			rescievedBytes.add(combinedAudio.getAudioData(VOLUME));
		}catch (OutOfMemoryError e) {
			onEverybodyLeave(g.getMember(g.getJDA().getSelfUser()).getVoiceState().getChannel());
		}
	}
	@Override
	public void handleUserAudio(UserAudio userAudio) {}
	@Override
	public void close(){
		try {
			int size=0;
			for (byte[] bs : rescievedBytes) {
				size+=bs.length;
			}
			byte[] decodedData=new byte[size];
			int i=0;
			for (byte[] bs : rescievedBytes) {
				for (int j = 0; j < bs.length; j++) {
					decodedData[i]=bs[j];
				}
			}
			getWavFile(getNextFile(), decodedData);
		} catch (IOException|OutOfMemoryError e) {
			e.printStackTrace();
		}
		
	}
	private void getWavFile(File outFile, byte[] decodedData) throws IOException {
        AudioFormat format = new AudioFormat(8000, 16, 1, true, false);
        AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(
                decodedData), format, decodedData.length), AudioFileFormat.Type.WAVE, outFile);
	}
}
