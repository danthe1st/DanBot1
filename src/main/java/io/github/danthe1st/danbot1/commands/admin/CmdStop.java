package io.github.danthe1st.danbot1.commands.admin;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.awt.Color;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Command to stop the Bot
 * @author Daniel Schmid
 */
@BotCommand("stop")
@SuppressFBWarnings(
		value="DM_EXIT", 
	    justification="I want to exit when stopping the program")
public class CmdStop implements Command{
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.checkOwner(event);	
	}
	@Override
	public void action(final String[] args, final MessageReceivedEvent event) {
		STATIC.msg(event.getTextChannel(), String.format(translate(event.getGuild(),"stopped"),STATIC.VERSION),Color.ORANGE,false);
		event.getJDA().shutdown();
		System.out.println("stopped by "+event.getAuthor());
		System.exit(0);
	}
	@Override
	public String help() {
		return "stopHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.ADMIN;
	}
}
