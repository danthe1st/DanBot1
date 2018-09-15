package commands.utils;


import commands.Command;
import commands.CommandType;
import core.PermsCore;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Ping Command(echos "Pong!" and the current Bot ping)
 * @author Daniel Schmid
 */
public class CmdPing implements Command{
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(!PermsCore.check(event, "ping")) {
			return;
		}
		STATIC.msg(event.getTextChannel(), "Pong",true);
		
		STATIC.msg(event.getTextChannel(), "my Ping: "+event.getJDA().getPing());
	}
	public String help(String prefix) {
		return "Pong!\n"
				+ "output Bot ping\n"
				+ "(see Permission *ping* in Command perm get)\n"
				+"*Syntax*: "+prefix+"ping";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}
}
