package commands.moderation;

import java.util.List;

import commands.BotCommand;
import commands.Command;
import commands.CommandType;
import core.PermsCore;
import io.github.danthe1st.util.STATIC;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
/**
 * Command to Change Guild-Roles
 * @author Daniel Schmid
 */
@BotCommand(aliases = "role")
public class CmdRole implements Command{
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		if (args.length==1) {
			return true;
		}
		return PermsCore.check(event, "role");
	}
	public void action(final String[] args, final MessageReceivedEvent event) {
		if (args.length==1) {
			List<Role> roles=STATIC.getRolesFromMsg(event.getMessage());
			for (Role role : roles) {
				if (!PermsCore.check(event, "role",false)) {
					if (!PermsCore.check(event, "role."+role.getName())) {
						return;
					}
				}
				
			}
			event.getGuild().getController().addRolesToMember(event.getGuild().getMember(event.getAuthor()), roles).queue();
			return;
		}
		if (args.length<2) {
			STATIC.errmsg(event.getTextChannel(), "missing args!");
			return;
		}
		final List<Role> roles = event.getGuild().getRolesByName(args[0], true);
		final List<Member> members = event.getGuild().getMembersByName(args[1], true);
		if (roles.isEmpty()) {
			STATIC.errmsg(event.getTextChannel(), "Role not found: "+args[0]);
		}
		if (members.isEmpty()) {
			STATIC.errmsg(event.getTextChannel(), "User not found: "+args[1]);
		}
		event.getGuild().getController().addRolesToMember(members.get(0), roles).queue();
	}
	public String help(String prefix) {
		return "Let a user join a role\n"
				+ "(see Permission *role* in Command perm get)\n"
				+"*Syntax*: "+prefix+"role <group> [<user>]";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.GUILD_MODERATION;
	}
}
