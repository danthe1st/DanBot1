package commands.botdata;

import java.awt.Color;

import commands.Command;
import core.PermsCore;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command forcguild-specified Bot-Permissions
 * @author Daniel Schmid
 *
 */
public class CmdPerm implements Command{
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		
		if(!PermsCore.check(event, "perm")) {
			return;
		}
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
			for (String perm : STATIC.getPerms(event.getGuild()).keySet()) {
				msg.appendDescription("**"+perm+"**:\t");
				boolean hasElementsBefore=false;
				for (String string : STATIC.getPerm(event.getGuild(), perm)) {
					if (hasElementsBefore) {
						msg.appendDescription("| ");
					}
					msg.appendDescription(string+" ");
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
			String[] groups = new String[args.length-2];
			for (int i = 2; i < args.length; i++) {
				groups[i-2] = args[i];
			}
			STATIC.setPerm(event.getGuild(), args[1], groups);
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
			if (STATIC.getPerm(event.getGuild(), args[1])==null) {
				STATIC.errmsg(event.getTextChannel(), "perm "+args[1]+" not found");
				return;
			}
			String[] groups = new String[args.length-2+STATIC.getPerm(event.getGuild(), args[1]).length];
			int i=0;
			for (i = 0; i < STATIC.getPerm(event.getGuild(), args[1]).length; i++) {
				groups[i] = STATIC.getPerm(event.getGuild(), args[1])[i];
			}
			for (int j=0; i < args.length-1; i++,j++) {
				groups[i] = args[j+2];
			}
			STATIC.setPerm(event.getGuild(), args[1], groups);
			break;
		}
		case "remove":
		case "rem":{
			if(!PermsCore.check(event, "perm.change")) {
				return;
			}
			if (args.length==2) {
				if (STATIC.getPerms(event.getGuild()).containsKey(args[1])) {
					if (STATIC.removePerm(event.getGuild(), args[1])) {
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
			String[] groups = new String[STATIC.getPerm(event.getGuild(), args[1]).length];
			
			for (int i = 0; i < STATIC.getPerm(event.getGuild(), args[1]).length; i++) {
				groups[i] = STATIC.getPerm(event.getGuild(), args[1])[i];
			}
			for (int i = 0; i < groups.length; i++) {
				for (String group : args) {
					if (groups[i].equals(group)) {
						groups[i]=null;
						break;
					}
				}
				
			}
			STATIC.setPerm(event.getGuild(), args[1], groups);
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
				for (String[] perms : STATIC.getPerms(event.getGuild()).values()) {
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
			STATIC.chRole(event.getGuild(), oldRole, newRole);
			break;
		}
		case "reset":{
			if(!PermsCore.check(event, "perm.change")) {
				return;
			}
			STATIC.resetPerms(event.getGuild());
			break;
		}
		case "reload":{
			if(!PermsCore.check(event, "perm.change")) {
				return;
			}
			STATIC.reloadPerms(event.getGuild());
			break;
		}
		default:
			STATIC.errmsg(event.getTextChannel(), help(STATIC.getPrefixExcaped(event.getGuild())));
			return;
		}
	}
	@Override
	public String help(String prefix) {
		return "get, set, remove, reset, reload or change a DanBot1-Permission Role Permissions\n"
				+ "(see *perm* Permissions in Command perm get)\n"
				+"*Syntax*: "+prefix+"perm get/show, reload, reset, set <Permission name> <Permission groups>,remove/rem <Permission name> (<Permission groups), changeRole <old Role> <new Role>";
	}
	@Override
	public String getCommandType() {
		return CMD_TYPE_BOT_MODERATION;
	}
}
