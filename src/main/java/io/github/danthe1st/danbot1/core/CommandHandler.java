package io.github.danthe1st.danbot1.core;

import java.util.HashMap;

import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.admin.CmdBlacklist;
import io.github.danthe1st.danbot1.core.CommandParser.CommandContainer;
import io.github.danthe1st.danbot1.util.STATIC;
import static io.github.danthe1st.danbot1.util.LanguageController.translate;

/**
 * executed by a listener when Message sent which begins with the Bot prefix
 * @author Daniel Schmid
 */
public class CommandHandler {
	public static final HashMap<String, Command> commands=new HashMap<String, Command>();
	/**
	 * loads Command and executes it
	 * @param cmd the Command as {@link CommandContainer}
	 */
	public static void handleCommand(final CommandParser.CommandContainer cmd) {
		if(commands.containsKey(cmd.invoke.toLowerCase())) {
			boolean blacklisted=CmdBlacklist.isBlacklisted(cmd.event.getAuthor().getId());
			if (blacklisted) {
				STATIC.errmsg(cmd.event.getTextChannel(), translate(cmd.event.getGuild(),"errBlacklisted"));
			}
			boolean save=(!blacklisted)&&commands.get(cmd.invoke.toLowerCase()).allowExecute(cmd.args, cmd.event);
			
			if(save) {
				try {
					commands.get(cmd.invoke.toLowerCase()).action(cmd.args, cmd.event);
				} catch (Exception e) {
					save=false;
				}
			}
			commands.get(cmd.invoke.toLowerCase()).executed(save, cmd.event);
		}
		else{
			STATIC.errmsg(cmd.event.getTextChannel(), translate(cmd.event.getGuild(),"unknownCommand"));
		}
	}
}
