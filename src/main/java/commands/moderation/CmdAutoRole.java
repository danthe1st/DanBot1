package commands.moderation;

import commands.Command;
import commands.CommandType;
import core.BotCommand;
import core.PermsCore;
import listeners.AutoRoleListener;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command to administrate autoroles<br>
 * An autorole is a role which is given to a user when joining the Guild (Discord Server)
 * @author Daniel Schmid
 */
@BotCommand(aliases = "autorole")
public class CmdAutoRole implements Command {

	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if(!PermsCore.check(event, "autorole")) {
			return;
		}
		if (args.length<1) {
			STATIC.errmsg(event.getTextChannel(), "not anough arguments");
			return;
		}
		switch (args[0]) {
		case "list":{
			StringBuilder sb=new StringBuilder("*Autoroles:*\n");
			for (Role role : AutoRoleListener.getRoles(event.getGuild())) {
				sb.append(role+"\n");
			}
			STATIC.msg(event.getTextChannel(), sb.toString());
			break;
		}
		case "add":{
			if (args.length<2) {
				STATIC.errmsg(event.getTextChannel(), "not anough arguments");
			}
			boolean empty=true;
			for (Role role : STATIC.getRolesFromMsg(event.getMessage())) {
				AutoRoleListener.addRole(role);
				empty=false;
			}
			if (empty) {
				STATIC.errmsg(event.getTextChannel(), "Cannot find any Roles.");
			}
			break;
		}
		case "remove":{
			if (args.length<2) {
				STATIC.errmsg(event.getTextChannel(), "not anough arguments");
			}
			
			boolean empty=true;
			for (Role role : STATIC.getRolesFromMsg(event.getMessage())) {
				AutoRoleListener.removeRole(role);
				empty=false;
			}
			if (empty) {
				STATIC.errmsg(event.getTextChannel(), "Cannot find any Roles.");
			}
			
			
			
			break;
		}
		default:
			break;
		}
	}

	@Override
	public String help(String prefix) {
		return "Sets, removes or lists autoroles(An autorole is a role which is given to a user when joining the Guild (Discord Server)\n"
				+ "(see Permission *autorole* in Command perm get)\n"
				+"*Syntax*: "+prefix+"autorole add/remove <name of the role>, list";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.GUILD_MODERATION;
	}
}
