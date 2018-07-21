package commands.utils;

import commands.Command;
import core.PermsCore;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command to echo a Message
 * @author Daniel Schmid
 */
public class CmdSay implements Command {
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(!PermsCore.check(event, "say")) {
			return;
		}		
		if(args.length<0) {
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
	public String getCommandType() {
		return CMD_TYPE_USER;
	}
}
