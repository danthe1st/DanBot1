package io.github.danthe1st.danbot1.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
/**
 * Implementation of {@link AudioSendHandler};
 * @author Daniel Schmid 
 */
public class PlayerSendHandler implements AudioSendHandler {

    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    public PlayerSendHandler(final AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }
    /**
     * tests if there is a Frame buffered
     */
    @Override
    public boolean canProvide() {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }
        return lastFrame != null;
    }
    /**
     * loads a buffered Frame
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
     * if the Audio is Opus Coded<br>
     * This music library does only use Opus
     */
    @Override
    public boolean isOpus() {
        return true;
    }
}