package io.github.danthe1st.danbot1.commands.admin;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * reloads the Bot(without stopping it)
 * @author Daniel Schmid
 */
@BotCommand(aliases = "reload")
public class CmdReload implements Command {
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.checkOwner(event);
	}
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		STATIC.loadData(event.getJDA());
	}

	@Override
	public String help() {
		return "reloadHelp";
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.ADMIN;
	}

}
