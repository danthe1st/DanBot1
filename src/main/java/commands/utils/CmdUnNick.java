package commands.utils;


import java.util.ArrayList;
import java.util.List;

import commands.Command;
import core.PermsCore;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import util.STATIC;
/**
 * Command to unnick a {@link Member}
 * @author Daniel Schmid
 */
public class CmdUnNick implements Command{
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if((args.length==0&&!PermsCore.check(event, "unnick",false))||(!PermsCore.check(event, "unnick.others"))) {
			return;
		}
		if (args.length==0) {
			try {
				event.getGuild().getController().setNickname(event.getGuild().getMember(event.getAuthor()), null).queue();
			} catch (InsufficientPermissionException e) {
				STATIC.errmsg(event.getTextChannel(), "I don't have the permission to unnick "+event.getGuild().getMember(event.getAuthor()).getEffectiveName()+", please contact an Administrator");
			}
			
			return;
		}
		//else
		List<Member> toNick=new ArrayList<>();
		for (String string : args) {
			try {
				toNick.add(event.getGuild().getMemberById(string));
			} catch (NumberFormatException e) {
				if(!toNick.addAll(event.getGuild().getMembersByEffectiveName(string, true))) {
					toNick.addAll(event.getGuild().getMembersByName(string, true));
				}
			}
		}
		for (Member member : toNick) {
			try {
				event.getGuild().getController().setNickname(member, null).queue();
			} catch (InsufficientPermissionException e) {
				STATIC.errmsg(event.getTextChannel(), "I don't have the permission to unnick "+member.getEffectiveName()+", please contact an Administrator");
			}
		}
	}
	@Override
	public String help(String prefix) {
		return "Unnicks Players\n"
				+ "if there are no players given the Author will be unnicked\n"
				+ "(see Permission *unnick(.others)* in Command perm get)\n"
				+ "*Syntax*: "+prefix+"unnick (<Player names>)";
	}
	@Override
	public String getCommandType() {
		return CMD_TYPE_GUILD_MODERATION;
	}
}
