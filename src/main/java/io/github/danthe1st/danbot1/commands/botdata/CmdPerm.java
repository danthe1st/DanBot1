package io.github.danthe1st.danbot1.commands.botdata;

import java.awt.Color;
import java.util.List;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
/**
 * Command forcguild-specified Bot-Permissions
 * @author Daniel Schmid
 */
@BotCommand(aliases = "perm")
public class CmdPerm implements Command{
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.check(event, "perm");
	}
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if(args.length<1) {
			STATIC.errmsg(event.getTextChannel(), "not enough arguments");
			return;
		}
		switch (args[0].toLowerCase()) {
		case "show":
		case "get":{
			if(!PermsCore.check(event, "perm.get")) {
				return;
			}
			
			EmbedBuilder msg=new EmbedBuilder()
					.setColor(Color.GREEN)
					.setDescription("**Permissions of Server "+event.getGuild().getName()+":**\n");
			for (String perm : PermsCore.getPerms(event.getGuild()).keySet()) {
				msg.appendDescription("**"+perm+"**:\t");
				boolean hasElementsBefore=false;
				for (String string : PermsCore.getPerm(event.getGuild(), perm)) {
					if (hasElementsBefore) {
						msg.appendDescription("| ");
					}
					msg.appendDescription(getNameFromRoleId(event.getGuild(), string)+" ");
					hasElementsBefore=true;
				}
				
				msg.appendDescription("\n");
			}
					
			event.getAuthor().openPrivateChannel().complete().sendMessage(msg.build()).complete();
			break;
		}
		case "set":{
			if(!PermsCore.check(event, "perm.change")) {
				return;
			}
			if (args.length<3) {
				STATIC.errmsg(event.getTextChannel(), "not enough arguments");
				return;
			}
//			String[] groups = new String[args.length-2];
			List<Role> roles=STATIC.getRolesFromMsg(event.getMessage());
			String[] groups=new String[roles.size()];
			for (int i = 0; i < groups.length; i++) {
				groups[i]=roles.get(i).getId();
			}
//			for (int i = 2; i < args.length; i++) {
//				groups[i-2] = args[i];
//			}
			PermsCore.setPerm(event.getGuild(), args[1],groups);
			break;
		}
		case "add":{
			if(!PermsCore.check(event, "perm.change")) {
				return;
			}
			if (args.length<3) {
				STATIC.errmsg(event.getTextChannel(), "not enough arguments");
				return;
			}
			if (PermsCore.getPerm(event.getGuild(), args[1])==null) {
				STATIC.errmsg(event.getTextChannel(), "perm "+args[1]+" not found");
				return;
			}
			String[] groups = new String[args.length-2+PermsCore.getPerm(event.getGuild(), args[1]).length];
			int i=0;
			for (i = 0; i < PermsCore.getPerm(event.getGuild(), args[1]).length; i++) {
				groups[i] = PermsCore.getPerm(event.getGuild(), args[1])[i];
			}
			for (int j=0; i < args.length-1; i++,j++) {
				groups[i] = PermsCore.getRoleFromName(args[j+2], event.getGuild());
			}
			PermsCore.setPerm(event.getGuild(), args[1], groups);
			break;
		}
		case "remove":
		case "rem":{
			if(!PermsCore.check(event, "perm.change")) {
				return;
			}
			if (args.length==2) {
				if (PermsCore.getPerms(event.getGuild()).containsKey(args[1])) {
					if (PermsCore.removePerm(event.getGuild(), args[1])) {
						STATIC.msg(event.getTextChannel(), "removed Permission "+args[1]);
						
						return;
					}
					STATIC.errmsg(event.getTextChannel(), "cannot remove Permission "+args[1]);
					
					return;
				}
			}
			if (args.length<3) {
				STATIC.errmsg(event.getTextChannel(), "not enough arguments");
				return;
			}
			String[] groups = new String[PermsCore.getPerm(event.getGuild(), args[1]).length];
			
			for (int i = 0; i < PermsCore.getPerm(event.getGuild(), args[1]).length; i++) {
				groups[i] = PermsCore.getPerm(event.getGuild(), args[1])[i];
			}
			for (int i = 0; i < groups.length; i++) {
				for (int j = 3; j < args.length; j++) {
					if (groups[i].equals(PermsCore.getRoleFromName(args[j], event.getGuild()))) {
						groups[i]=null;
						break;
					}
				}
				
			}
			PermsCore.setPerm(event.getGuild(), args[1], groups);
			break;
		}
		case "chrole":
		case "changerole":{
			if(!PermsCore.check(event, "perm.change")) {
				return;
			}
			if (args.length<3) {
				STATIC.errmsg(event.getTextChannel(), "not enough arguments");
				
				return;
			}
			int newRoleStart=2;
			String oldRoleTmp=args[1];
			String oldRole=oldRoleTmp;
			loop:for (int i = 1; i < args.length-1; i++) {
				for (String[] perms : PermsCore.getPerms(event.getGuild()).values()) {
					for (int j = 0; j < perms.length; j++) {
						if (perms[j].equals(oldRoleTmp)) {
							oldRole=oldRoleTmp;
							newRoleStart=i+1;
							break loop;
						}
					}
					
				}
				oldRoleTmp+=" "+args[i+1];
			}
			String newRole=args[newRoleStart];
			for (int i = newRoleStart+1; i < args.length; i++) {
				newRole+=" "+args[i];
			}
			PermsCore.chRole(event.getGuild(), oldRole, newRole);
			break;
		}
		case "reset":{
			if(!PermsCore.check(event, "perm.change")) {
				return;
			}
			PermsCore.resetPerms(event.getGuild());
			break;
		}
		case "reload":{
			if(!PermsCore.check(event, "perm.change")) {
				return;
			}
			PermsCore.reloadPerms(event.getGuild());
			break;
		}
		default:
			STATIC.errmsg(event.getTextChannel(), help(STATIC.getPrefixEscaped(event.getGuild())));
			return;
		}
	}
	private String getNameFromRoleId(Guild g,String id) {
		try {
			return g.getRoleById(id).getName();
		} catch (Exception e) {
			return id;
		}
		
	}
	@Override
	public String help(String prefix) {
		return "get, set, remove, reset, reload or change a DanBot1-Permission Role Permissions\n"
				+ "(see *perm* Permissions in Command perm get)\n"
				+"*Syntax*: "+prefix+"perm get/show, reload, reset, set <Permission name> <Permission groups>,remove/rem <Permission name> (<Permission groups), changeRole <old Role> <new Role>";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.BOT_MODERATION;
	}
}
