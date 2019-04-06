package io.github.danthe1st.danbot1.commands.audio.userphone;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@BotCommand(aliases = {"userphone","vbridge"})
public class CmdUserphone implements Command{

	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.check(event, "userphone");
	}
	
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if (args.length<1) {
			STATIC.errmsg(event.getTextChannel(), "not anough arguments");
			return;
		}
		switch (args[0].toLowerCase()) {
		case "open":
			if (UserphoneController.canOpenConnection(event.getGuild())) {
				UserphoneController.openUserphoneConnection(event.getGuild().getMember(event.getAuthor()).getVoiceState().getChannel());
			}
			break;
		case "close":
			if (UserphoneController.canCloseConnection(event.getGuild())) {
				UserphoneController.closeUserphoneConnection(event.getGuild());
			}
			break;
		default:
			break;
		}
	}

	@Override
	public String help(String prefix) {
		return "opens/closes an userphone connection\n"
				+ "(see Permission *userphone* in Command perm get)\n"
				+ "*Syntax*: "+prefix+"userphone open/close";
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}
	
}
