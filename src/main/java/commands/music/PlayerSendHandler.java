package commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.core.audio.AudioSendHandler;
/**
 * Implementierung von net.dv8tion.jda.core.audio.AudioSendHandler;
 * 
 * @author Daniel Schmid 
 *
 */
public class PlayerSendHandler implements AudioSendHandler {

    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    public PlayerSendHandler(final AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    /**
     * kann Sound abgespielt werden bzw. wird Sound abgespielt?
     */
    @Override
    public boolean canProvide() {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }
        return lastFrame != null;
    }
    /**
     * lädt einen Frame und gibt diesen zurück
     */
    @Override
    public byte[] provide20MsAudio() {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }

        final byte[] data = lastFrame != null ? lastFrame.getData() : null;
        lastFrame = null;

        return data;
    }

    /**
     * Standardwert <code>true</code>
     */
    @Override
    public boolean isOpus() {
        return true;
    }
}