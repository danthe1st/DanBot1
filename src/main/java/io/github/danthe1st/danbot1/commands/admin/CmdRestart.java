package io.github.danthe1st.danbot1.commands.admin;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.io.File;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.Main;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Command to restart the Bot
 * @author Daniel Schmid
 */
@BotCommand("restart")
@SuppressFBWarnings(
		value="DM_EXIT", 
	    justification="I want to exit when restarting the program")
public class CmdRestart implements Command{
	private static String[] startfiles=new String[] {"DanBot1.bat","DanBot1.sh"};
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.checkOwner(event);	
	}
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		STATIC.msg(event.getTextChannel(), translate(event.getGuild(),"restarting")+STATIC.VERSION, false);		
		Command.super.executed(true, event);
		event.getJDA().shutdown();
		String restartCommand=getRestartCommand();
		if (restartCommand!=null) {
			try {
				
				System.out.println("restarting with following: "+restartCommand);
				Runtime.getRuntime().exec(restartCommand);
				System.exit(0);
			} catch (Exception e) {
				System.out.println("Cannot restart this way.");
			}
		}
		
		System.out.println("restarting manually");
		Main.main(Main.getArgs());
		return;
	}
	/**
	 * looks for the Command({@link Runtime#exec(String)}) to restart the Bot
	 * @return the Command
	 */
	private String getRestartCommand() {
		String command=System.getProperty("restartcommand");
		if (command!=null) {
			return command;
		}
		File currentJar=null;
		try {
			
			for (String startfile : startfiles) {
				File file=new File(startfile);
				if (file.exists()&&file.canExecute()) {
					return file.getAbsolutePath();
				}
			}
			currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			if (currentJar.getName().endsWith(".jar")) {
				StringBuilder commandBuilder=new StringBuilder("java -jar ").append(currentJar.getAbsolutePath());
				for (String arg : Main.getArgs()) {
					commandBuilder.append(" ").append(arg);
				}
				return commandBuilder.toString();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public String help() {
		return "restartHelp";
	}
	@Override
	public void executed(boolean success, MessageReceivedEvent event) {}
	@Override
	public CommandType getCommandType() {
		return CommandType.ADMIN;
	}
}
