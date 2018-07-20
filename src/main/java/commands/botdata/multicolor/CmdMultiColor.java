package commands.botdata.multicolor;

import java.util.List;

import commands.Command;
import core.PermsCore;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;
import util.STATIC;
/**
 * Befehl f. setzen/löschen von Multicolor-Rollen
 * @author Daniel Schmid
 *
 */
public class CmdMultiColor implements Command{

	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if(!PermsCore.check(event, "multicolor.set")) {
			return;
		}		
		if (args.length<2) {
			STATIC.errmsg(event.getTextChannel(), "missing args!");
			return;
		}
		switch (args[0]) {
		case "add":{
			
			Role role=getRole(event.getGuild(), args,1);
			if (role==null) {
				STATIC.errmsg(event.getTextChannel(), "Role not found.");
			}
			if(!PermissionUtil.canInteract(event.getGuild().getMember(event.getJDA().getSelfUser()), role)) {
				STATIC.errmsg(event.getTextChannel(), "I don't have Permissions to change Colors of this User.\n"
						+ "My Role is under the Role "+role.getName());
			}
			if (!PermissionUtil.checkPermission(event.getGuild().getMember(event.getJDA().getSelfUser()), Permission.MANAGE_ROLES)) {
				STATIC.errmsg(event.getTextChannel(), "I don't have Permissions to change Colors of this User.\n"
						+ "I don't have the Permission `"+Permission.MANAGE_ROLES.name()+"`");
			}
			MultiColorChanger.addMultiColorRole(role);
			break;
		}
		case "remove":{
			Role role=getRole(event.getGuild(), args,1);
			if (role==null) {
				STATIC.errmsg(event.getTextChannel(), "Role not found.");
			}
			MultiColorChanger.removeMultiColorRole(role);
			
			break;
		}
		case "list":{
			List<Role> roles=MultiColorChanger.getMultiColorRoles(event.getGuild());
			StringBuilder sb=new StringBuilder("Multicolor Roles in "+event.getGuild().getName());
			boolean hasElementsBefore=false;
			for (Role role : roles) {
				if (hasElementsBefore) {
					sb.append(" | ");
					hasElementsBefore=true;
				}
				sb.append(role.getName());
				
			}
			break;
		}
		default:
			break;
		}
	}
	private Role getRole(Guild g, String[] arr, int beginIndex) {
		Role role=null;
		try {
			role=g.getRoleById(arr[beginIndex]);
		} catch (NumberFormatException e) {
		}
		if (role==null) {
			StringBuilder roleBuilder=new StringBuilder();
			for (int i = beginIndex; i < arr.length; i++) {
				roleBuilder.append(arr[i]);
				if (i<arr.length-1) {
					roleBuilder.append(" ");
				}
			}
			List<Role> roles=g.getRolesByName(roleBuilder.toString(), true);
			if (roles.isEmpty()) {
				return null;
			}
			return roles.get(0);
		}
		return role;
	}
	@Override
	public String help(String prefix) {
		return "Sets a role as a Multicolor role\n"
				+ "A multicolor Role is a Discord Role changes the Color all the time\n"
				+ "(see Permission *multicolor.set* in Command perm get)\n"
				+"*Syntax*: "+prefix+"multicolor add <Role>/remove <Role>";
	}
	@Override
	public String getCommandType() {
		return CMD_TYPE_GUILD_MODERATION;
	}
}
