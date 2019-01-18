package commands.utils;

import commands.BotCommand;
import commands.Command;
import commands.CommandType;
import core.PermsCore;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command to echo a Message
 * @author Daniel Schmid
 */
@BotCommand(aliases = "say")
public class CmdSay implements Command {
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(!PermsCore.check(event, "say")) {
			return;
		}		
		if(args.length<1) {
			STATIC.errmsg(event.getTextChannel(), "not anough arguments");
		}
		String msg="";
		for (final String string : args) {
			msg+=string+" ";
		}
		STATIC.msg(event.getTextChannel(), msg);
	}
	public String help(String prefix) {
		return "Write a message in the chat"
				+ "(see Permission *say* in Command perm get)\n"
				+"*Syntax*: "+prefix+"say <message>";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}
}
