package commands.admin;

import commands.Command;
import commands.CommandType;
import core.BotCommand;
import core.PermsCore;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;

@BotCommand(aliases = "reload")
public class CmdReload implements Command {//TODO doc
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
