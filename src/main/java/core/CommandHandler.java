package core;

import java.util.HashMap;

import commands.Command;
import util.STATIC;
/**
 * wird v. Listener aufgerufen wenn Chat-Nachricht mit Pr�fix beginnt
 * @author Daniel Schmid
 *
 */
public class CommandHandler {
	public static final CommandParser parse=new CommandParser();
	public static HashMap<String, Command> commands=new HashMap<String, Command>();
	/**
	 * Methode die den Command ausführt(+executed...)
	 * @param cmd Der aufgeteilte Command
	 */
	public static void handleCommand(final CommandParser.CommandContainer cmd) {
		if(commands.containsKey(cmd.invoke.toLowerCase())) {
			boolean save=commands.get(cmd.invoke.toLowerCase()).allowExecute(cmd.args, cmd.event);//nicht wichtig
			
			if(save) {
				try {
					commands.get(cmd.invoke.toLowerCase()).action(cmd.args, cmd.event);
				} catch (Exception e) {
					save=false;
				}
					commands.get(cmd.invoke.toLowerCase()).executed(save, cmd.event);
				
			}
			else {
				commands.get(cmd.invoke.toLowerCase()).executed(save, cmd.event);
			}
		}
		else{
			STATIC.errmsg(cmd.event.getTextChannel(), "That Command is not not known.");
//			final Message msg=cmd.event.getTextChannel().sendMessage(
//					new EmbedBuilder()
//					.setDescription("That Command is not not known.")
//					.setColor(Color.red)
//					.build()).complete();
//			new Timer().schedule(new TimerTask() {
//				
//				@Override
//				public void run() {
//					msg.delete().queue();
//					
//				}
//			}, STATIC.INFO_TIMEOUT);
		}
	}
}
