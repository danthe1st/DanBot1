package commands.moderation;

import net.dv8tion.jda.api.entities.Member;

import java.util.List;

import commands.BotCommand;
import commands.Command;
import commands.CommandType;
import core.PermsCore;
import io.github.danthe1st.util.STATIC;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
/**
 * Command to kick a {@link Member}
 * @author Daniel Schmid
 */
@BotCommand(aliases = "kick")
public class CmdKick implements Command{
	
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.check(event, "kick");
	}
	public void action(final String[] args, final MessageReceivedEvent event) {
		if (args.length<1) {
			STATIC.errmsg(event.getTextChannel(), "missing args!");
			return;
		}
			List<Member> users= event.getGuild().getMembersByName(args[0], true);
			String reason=null;
			if (args.length>1) {
				reason="";
				for (int i = 1; i < args.length; i++) {
					reason=reason+args[i];
					
				}
			}
			for (Member user : users) {
				try {
					event.getGuild().getController().kick(user, reason).queue();
				} catch (Exception e) {
					STATIC.errmsg(event.getTextChannel(), "unknown Error kicking user "+user.getNickname());
				}
				
		}
		
		
	}
	public String help(String prefix) {
		return "kicks a user\n"
				+ "(see Permission *kick* in Command perm get)\n"
				+"*Syntax*: "+prefix+"kick <victim>";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.GUILD_MODERATION;
	}
}
