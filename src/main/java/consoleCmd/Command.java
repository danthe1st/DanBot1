package consoleCmd;

import net.dv8tion.jda.core.JDA;
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
