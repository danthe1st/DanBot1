package commands.botdata;


import commands.BotCommand;
import commands.Command;
import commands.CommandType;
import core.PermsCore;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command to change/show the Bot prefix
 * @author Daniel Schmid
 *
 */
@BotCommand(alias = "prefix")
public class CmdPrefix implements Command{
	@Override
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(!PermsCore.check(event, "prefix")) {
			return;
		}
		if(args.length<1) {
			STATIC.errmsg(event.getTextChannel(), "Not anough arguments.");
			return;
		}
		switch (args[0]) {
		case "show":
			if(!PermsCore.check(event, "prefix.show")) {
				return;
			}
			STATIC.msg(event.getTextChannel(), STATIC.getPrefix(event.getGuild()));
			break;
		case "set":
			if(!PermsCore.check(event, "prefix.set")) {
				return;
			}
			if(args.length<2) {
				STATIC.errmsg(event.getTextChannel(), "Not anough arguments.");
				return;
			}
			STATIC.setPrefix(event.getGuild(),args[1]);
			break;
		default:
			STATIC.errmsg(event.getTextChannel(), "wrong arguments");
			break;
		}
	}
	@Override
	public String help(String prefix) {
		return "set or show the preifx\n"
				+ "(see Permissions *perfix(.show/set)* in Command perm get)\n"
				+"*Syntax*: "+prefix+"prefix show, set <new Prefix>";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.BOT_MODERATION;
	}
}
