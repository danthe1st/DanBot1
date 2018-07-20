package consoleCmd;

import net.dv8tion.jda.core.JDA;

public interface Command {

	public void execute(JDA jda, String[] args);
	public String help();
	
	
}
