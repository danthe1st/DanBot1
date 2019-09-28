package io.github.danthe1st.danbot1.commands.audio;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

/**
 * An interface for Objects that hold an Audio Handler for a {@link Guild}
 * @author Daniel Schmid
 */
public interface AudioHolder {
	/**
	 * closes the audio Connection if another {@link AudioHolder} needs it
	 * @param g the {@link Guild} where the Connection is closed
	 */
	public void closeConnection(Guild g);
	/**
	 * a method that is called when the last member, that is not a bot, leaves the {@link VoiceChannel}, where the Bot is in.<br>
	 * This may closes the Connection
	 * @param vc the left {@link VoiceChannel}
	 */
	public default void onEverybodyLeave(VoiceChannel vc) {}
}
