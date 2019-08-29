package io.github.danthe1st.danbot1.commands.utils;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Command to echo a Message
 * @author Daniel Schmid
 */
@BotCommand("say")
public class CmdSay implements Command {
	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return PermsCore.check(event, "say");
	}
	public void action(final String[] args, final GuildMessageReceivedEvent event) {
		if(!PermsCore.check(event, "say")) {
			return;
		}		
		if(args.length<1) {
			STATIC.errmsg(event.getChannel(), translate(event.getGuild(),"missingArgs"));
		}
		StringBuilder msg=new StringBuilder();
		for (final String string : args) {
			msg.append(string).append(" ");
		}
		STATIC.msg(event.getChannel(), msg.toString());
	}
	public String help() {
		return "sayHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}
}
