package io.github.danthe1st.danbot1.commands.moderation;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.listeners.AutoRoleListener;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Command to administrate autoroles<br>
 * An autorole is a role which is given to a user when joining the Guild (Discord Server)
 * @author Daniel Schmid
 */
@BotCommand("autorole")
public class CmdAutoRole implements Command {
	private static final String MISSING_ARGS_TRANSLATER="missingArgs";
	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return PermsCore.check(event, "autorole");
	}
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if (args.length<1) {
			STATIC.errmsg(event.getChannel(), translate(event.getGuild(),MISSING_ARGS_TRANSLATER));
			return;
		}
		switch (args[0]) {
		case "list":{
			StringBuilder sb=new StringBuilder(translate(event.getGuild(),"autoroleTitle")).append('\n');
			for (Role role : AutoRoleListener.getRoles(event.getGuild())) {
				sb.append(role+"\n");
			}
			STATIC.msg(event.getChannel(), sb.toString());
			break;
		}
		case "add":{
			if (args.length<2) {
				STATIC.errmsg(event.getChannel(), translate(event.getGuild(),MISSING_ARGS_TRANSLATER));
			}
			boolean empty=true;
			for (Role role : STATIC.getRolesFromMsg(event.getMessage())) {
				AutoRoleListener.addRole(role);
				empty=false;
			}
			if (empty) {
				STATIC.errmsg(event.getChannel(), translate(event.getGuild(),"noRolesFound"));
			}
			break;
		}
		case "remove":{
			if (args.length<2) {
				STATIC.errmsg(event.getChannel(), translate(event.getGuild(),MISSING_ARGS_TRANSLATER));
			}
			
			boolean empty=true;
			for (Role role : STATIC.getRolesFromMsg(event.getMessage())) {
				AutoRoleListener.removeRole(role);
				empty=false;
			}
			if (empty) {
				STATIC.errmsg(event.getChannel(), translate(event.getGuild(),"noRolesFound"));
			}
			
			
			
			break;
		}
		default:
			break;
		}
	}

	@Override
	public String help() {
		return "autoRoleHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.GUILD_MODERATION;
	}
}
