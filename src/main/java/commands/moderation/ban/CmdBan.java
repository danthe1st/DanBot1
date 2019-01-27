package commands.moderation.ban;

import net.dv8tion.jda.api.entities.Member;

import java.util.List;

import commands.BotCommand;
import commands.Command;
import commands.CommandType;
import core.PermsCore;
import io.github.danthe1st.util.STATIC;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
/**
 * Command to ban a {@link Member}
 * @author Daniel Schmid
 *
 */
@BotCommand(aliases = "ban")
public class CmdBan implements Command{
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.check(event, "ban");
	}
	public void action(final String[] args, final MessageReceivedEvent event) {
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
	public CommandType getCommandType() {
		return CommandType.GUILD_MODERATION;
	}
}
