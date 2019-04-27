package io.github.danthe1st.danbot1.commands.audio;

import net.dv8tion.jda.api.entities.Guild;

public interface AudioHolder {
	public void closeConnection(Guild g);
	public default void onEverybodyLeave(Guild g) {}
}
