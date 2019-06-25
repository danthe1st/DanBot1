package io.github.danthe1st.danbot1.commands.moderation;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.util.List;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Command to Change Guild-Roles
 * @author Daniel Schmid
 */
@BotCommand("role")
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
			Member member=event.getGuild().getMember(event.getAuthor());
			for (Role role : roles) {
				event.getGuild().addRoleToMember(member, role).queue();
			}
			return;
		}
		if (args.length<2) {
			STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"missingArgs"));
			return;
		}
		final List<Role> roles = event.getGuild().getRolesByName(args[0], true);
		final List<Member> members = event.getGuild().getMembersByName(args[1], true);
		if (roles.isEmpty()) {
			STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"errRoleNotFound")+args[0]);
		}
		if (members.isEmpty()) {
			STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"errUserNotFound")+args[1]);
		}
		Member member=members.get(0);
		for (Role role : roles) {
			event.getGuild().addRoleToMember(member, role).queue();
		}
	}
	public String help() {
		return "roleHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.GUILD_MODERATION;
	}
}
