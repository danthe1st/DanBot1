package io.github.danthe1st.danbot1.commands.moderation;

import net.dv8tion.jda.api.entities.Member;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.util.List;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
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
			STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"missingArgs"));
			return;
		}
			List<Member> users= event.getGuild().getMembersByName(args[0], true);
			String reason=null;
			if (args.length>1) {
				StringBuilder reasonBuilder=new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					reasonBuilder.append(args[i]);
				}
				reason=reasonBuilder.toString();
			}
			for (Member user : users) {
				try {
					event.getGuild().kick(user, reason).queue();
				} catch (Exception e) {
					STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"errCannotKick")+user.getNickname());
				}
				
		}
		
		
	}
	public String help() {
		return "kickHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.GUILD_MODERATION;
	}
}
