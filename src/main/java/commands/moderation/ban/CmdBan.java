package commands.moderation.ban;

import net.dv8tion.jda.core.entities.Member;

import java.util.List;

import commands.Command;
import core.PermsCore;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command to ban a {@link Member}
 * @author Daniel Schmid
 *
 */
public class CmdBan implements Command{

	

	/**
	 * Der Befehl selbst(siehe help)
	 */
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(!PermsCore.check(event, "ban")) {
			return;
		}		
		if (args.length<1) {
			STATIC.errmsg(event.getTextChannel(), "missing args!");
			return;
		}
			List<Member> users= event.getGuild().getMembersByName(args[0], true);
			String reason=null;
			int time=0;
			int argCount=1;
			if (args.length>argCount) {
				reason="";
				for (int i = argCount; i < args.length; i++) {
					reason=reason+args[i];
					
				}
			}
			for (Member user : users) {
				
				try {
					event.getGuild().getController().ban(user,time, reason).queue();
				} catch (Exception e) {
					STATIC.errmsg(event.getTextChannel(), "unknown Error banning user "+user.getNickname());
				}
				
		}
		
		
	}
	public String help(String prefix) {
		return "bans a user (see Permission *ban* in Command perm get)\n"
				+"*Syntax*: "+prefix+"ban <victim> (<reason>)";
	}
	@Override
	public String getCommandType() {
		return CMD_TYPE_GUILD_MODERATION;
	}
}
