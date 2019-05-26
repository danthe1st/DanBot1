package io.github.danthe1st.danbot1.console;

import net.dv8tion.jda.api.JDA;
/**
 * Interface for Console Commands
 * @author Daniel Schmid
 */
public interface Command {
	/**
	 * the Command
	 * @param jda the JDA instance
	 * @param args the Command arguments
	 */
	public void execute(JDA jda, String[] args);
	/**
	 * get help for a Command
	 * @return the Help
	 */
	public String help();
	
	
}
