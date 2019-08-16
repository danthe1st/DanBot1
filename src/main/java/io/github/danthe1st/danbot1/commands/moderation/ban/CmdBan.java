package io.github.danthe1st.danbot1.commands.moderation.ban;

import net.dv8tion.jda.api.entities.Member;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.util.List;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Command to ban a {@link Member}
 * @author Daniel Schmid
 */
@BotCommand("ban")
public class CmdBan implements Command{
	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return PermsCore.check(event, "ban");
	}
	public void action(final String[] args, final GuildMessageReceivedEvent event) {
		if (args.length<1) {
			STATIC.errmsg(event.getChannel(), translate(event.getGuild(),"missingArgs"));
			return;
		}
			List<Member> users= event.getGuild().getMembersByName(args[0], true);
			String reason=null;
			int time=0;
			int argCount=1;
			if (args.length>argCount) {
				StringBuilder reasonBuilder=new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					reasonBuilder.append(args[i]);
				}
				reason=reasonBuilder.toString();
			}
			for (Member user : users) {
				
				try {
					event.getGuild().ban(user,time, reason).queue();
				} catch (Exception e) {
					STATIC.errmsg(event.getChannel(), translate(event.getGuild(),"errCannotBan")+user.getNickname());
				}
				
		}
		
		
	}
	public String help() {
		return "banHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.GUILD_MODERATION;
	}
}
