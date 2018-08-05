package commands.multicolor;

import java.util.List;

import commands.Command;
import core.PermsCore;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;
import util.STATIC;
/**
 * Command to administrate multicolor Roles
 * @author Daniel Schmid
 */
public class CmdMultiColor implements Command{

	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if(!PermsCore.check(event, "multicolor.set")) {
			return;
		}		
		if (args.length<1) {
			STATIC.errmsg(event.getTextChannel(), "missing args!");
			return;
		}
		switch (args[0]) {
		case "add":{
			if (args.length<2) {
				STATIC.errmsg(event.getTextChannel(), "missing args!");
				return;
			}
			List<Role> roles=STATIC.getRolesFromMsg(event.getMessage());
			if (roles.isEmpty()) {
				STATIC.errmsg(event.getTextChannel(), "Role not found.");
			}
			for (Role role : roles) {
				if(!PermissionUtil.canInteract(event.getGuild().getMember(event.getJDA().getSelfUser()), role)) {
					STATIC.errmsg(event.getTextChannel(), "I don't have Permissions to change Colors of this User.\n"
							+ "My Role is under the Role "+role.getName());
					return;
				}
				if (!PermissionUtil.checkPermission(event.getGuild().getMember(event.getJDA().getSelfUser()), Permission.MANAGE_ROLES)) {
					STATIC.errmsg(event.getTextChannel(), "I don't have Permissions to change Colors of this User.\n"
							+ "I don't have the Permission `"+Permission.MANAGE_ROLES.name()+"`");
					return;
				}
				MultiColorChanger.addMultiColorRole(role);
			}
			
			break;
		}
		case "remove":{
			if (args.length<2) {
				STATIC.errmsg(event.getTextChannel(), "missing args!");
				return;
			}
			List<Role> roles=STATIC.getRolesFromMsg(event.getMessage());
			if (roles.isEmpty()) {
				STATIC.errmsg(event.getTextChannel(), "Role not found.");
			}
			for (Role role : roles) {
				MultiColorChanger.removeMultiColorRole(role);
			}
			
			
			break;
		}
		case "list":{
			List<Role> roles=MultiColorChanger.getMultiColorRoles(event.getGuild());
			StringBuilder sb=new StringBuilder("**Multicolor Roles**\n");
			boolean hasElemBefore=false;
			for (Role role : roles) {
				sb.append(role.getName());
				if (hasElemBefore) {
					sb.append(" | ");
					hasElemBefore=true;
				}
				
			}
			
			STATIC.msg(event.getTextChannel(),sb.toString());
			break;
		}
		default:{
			STATIC.errmsg(event.getTextChannel(), "Wrong arguments");
			break;
		}
			
			
		}
	}
	
	@Override
	public String help(String prefix) {
		return "Sets, unsets or lists Multicolor roles\n"
				+ "A multicolor Role is a Discord Role changes the Color all the time\n"
				+ "(see Permission *multicolor.set* in Command perm get)\n"
				+"*Syntax*: "+prefix+"multicolor list, add <Role>, remove <Role>";
	}
	@Override
	public String getCommandType() {
		return CMD_TYPE_GUILD_MODERATION;
	}
}
