package commands;

import java.io.File;

import com.sun.javafx.geom.transform.GeneralTransform3D;

import core.Main;
import core.PermsCore;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command to restart the Bot
 * @author Daniel Schmid
 */
public class CmdRestart implements Command{
	private static String[] startfiles=new String[] {"DanBot1.bat","DanBot1.sh"};
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if (!PermsCore.check(event, "restart")) {
			return;
		}
		STATIC.msg(event.getTextChannel(), "restarting DanBot1 "+STATIC.VERSION, false);		
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
				command= "java -jar "+currentJar.getAbsolutePath();
				for (String arg : Main.getArgs()) {
					command+=" "+arg;
					
				}
				return command;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public String help(String prefix) {
		return "restarts the bot\n"
				+ "(see Permission *restart* in Command perm get)\n"
				+ "*Syntax*: "+prefix+"restart";
	}
	@Override
	public void executed(boolean success, MessageReceivedEvent event) {
		return;
	}
	@Override
	public String getCommandType() {
		return CMD_TYPE_BOT_MODERATION;
	}
}
