package commands.admin;

import commands.BotCommand;
import commands.Command;
import commands.CommandType;
import core.PermsCore;
import io.github.danthe1st.util.STATIC;
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
	public String help(String prefix) {
		return "reloads Guild Files\n"
				+ "**CAN ONLY BE USED BY *the bot-admin***";
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.ADMIN;
	}

}
