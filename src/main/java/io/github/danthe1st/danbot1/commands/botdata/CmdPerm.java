package io.github.danthe1st.danbot1.commands.botdata;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
/**
 * Command forcguild-specified Bot-Permissions
 * @author Daniel Schmid
 */
@BotCommand("perm")
public class CmdPerm implements Command{
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.check(event, "perm");
	}
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if(args.length<1) {
			STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"missingArgs"));
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
					.setDescription(String.format(translate(event.getGuild(),"**Permissions of Server %s:**\n"),event.getGuild().getName()));
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
				STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"missingArgs"));
				return;
			}
			List<String> roles=new ArrayList<>();
			for (int i = 2; i < args.length; i++) {
				String roleID=PermsCore.getRoleIDFromName(args[i], event.getGuild());
				if (roleID!=null) {
					roles.add(roleID);
				}
			}
			String[] groups=new String[roles.size()];
			for (int i = 0; i < groups.length; i++) {
				groups[i]=roles.get(i);
			}
			PermsCore.setPerm(event.getGuild(), args[1],groups);
			break;
		}
		case "add":{
			if(!PermsCore.check(event, "perm.change")) {
				return;
			}
			if (args.length<3) {
				STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"missingArgs"));
				return;
			}
			if (PermsCore.getPerm(event.getGuild(), args[1])==null) {
				STATIC.errmsg(event.getTextChannel(), String.format(translate(event.getGuild(),"permNotFound"),args[1]));
				return;
			}
			String[] groups = new String[args.length-2+PermsCore.getPerm(event.getGuild(), args[1]).length];
			int i=0;
			for (i = 0; i < PermsCore.getPerm(event.getGuild(), args[1]).length; i++) {
				groups[i] = PermsCore.getPerm(event.getGuild(), args[1])[i];
			}
			for (int j=0; i < args.length-1; i++,j++) {
				groups[i] = PermsCore.getRoleIDFromName(args[j+2], event.getGuild());
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
						STATIC.msg(event.getTextChannel(), translate(event.getGuild(),"permDeleted")+args[1]);
						return;
					}
					STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"permDeleteFailed")+args[1]);
					return;
				}
			}
			if (args.length<3) {
				STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"missingArgs"));
				return;
			}
			String[] groups = new String[PermsCore.getPerm(event.getGuild(), args[1]).length];
			
			for (int i = 0; i < PermsCore.getPerm(event.getGuild(), args[1]).length; i++) {
				groups[i] = PermsCore.getPerm(event.getGuild(), args[1])[i];
			}
			for (int i = 0; i < groups.length; i++) {
				for (int j = 3; j < args.length; j++) {
					if (groups[i].equals(PermsCore.getRoleIDFromName(args[j], event.getGuild()))) {
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
				STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"missingArgs"));
				
				return;
			}
			int newRoleStart=2;
			StringBuilder oldRoleTmp=new StringBuilder(args[1]);
			
			String oldRole=oldRoleTmp.toString();
			loop:for (int i = 1; i < args.length-1; i++) {
				for (String[] perms : PermsCore.getPerms(event.getGuild()).values()) {
					for (int j = 0; j < perms.length; j++) {
						if (perms[j].equals(oldRoleTmp.toString())) {
							oldRole=oldRoleTmp.toString();
							newRoleStart=i+1;
							break loop;
						}
					}
					
				}
				oldRoleTmp.append(" ").append(args[i+1]);
			}
			StringBuilder newRole=new StringBuilder(args[newRoleStart]);
			for (int i = newRoleStart+1; i < args.length; i++) {
				newRole.append(" ").append(args[i]);
			}
			PermsCore.chRole(event.getGuild(), oldRole, newRole.toString());
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
			STATIC.errmsg(event.getTextChannel(), help().replace("--",STATIC.getPrefixEscaped(event.getGuild())));
			return;
		}
	}
	/**
	 * gets the name of a {@link Role}
	 * @param g the {@link Guild} the role is in
	 * @param id the {@link ISnowflake} ID of the {@link Role}
	 * @return the name of the Role
	 */
	private String getNameFromRoleId(Guild g,String id) {
		try {
			return g.getRoleById(id).getName();
		} catch (Exception e) {
			return id;
		}
		
	}
	@Override
	public String help() {
		return "permHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.BOT_MODERATION;
	}
}
