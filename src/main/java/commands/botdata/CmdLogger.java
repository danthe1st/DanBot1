package commands.botdata;

import commands.Command;
import commands.CommandType;
import core.PermsCore;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command to set the Logger-Channel
 * @author Daniel Schmid
 *
 */
public class CmdLogger implements Command{
	@Override
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(!PermsCore.check(event, "logger")) {
			return;
		}
		if(args.length<1) {
			STATIC.errmsg(event.getTextChannel(), "Not anough arguments.");
			return;
		}
		switch (args[0]) {
		case "show":
			if(!PermsCore.check(event, "logger.show")) {
				return;
			}
			STATIC.msg(event.getTextChannel(), "Der Command-Log-Channel ist "+STATIC.getCmdLogger(event.getGuild()));
			
			break;
		case "set":
			if(!PermsCore.check(event, "logger.set")) {
				return;
			}
			if(args.length<2) {
				STATIC.errmsg(event.getTextChannel(), "Not anough arguments.");
				return;
			}
			STATIC.setCmdLogger(event.getGuild(), args[1]);
			break;
		default:
			STATIC.errmsg(event.getTextChannel(), "wrong arguments");
			break;
		}
	}
	@Override
	public String help(String prefix) {
		return "set or show the Logger Channel\n"
				+ "(see Permission *logger(.set/show)* in Command perm get)\n"
				+"*Syntax*: "+prefix+"cmdLogger show, set <new cmdLogger>";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.BOT_MODERATION;
	}
}
