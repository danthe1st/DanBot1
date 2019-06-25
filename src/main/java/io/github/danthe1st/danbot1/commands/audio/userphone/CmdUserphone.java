package io.github.danthe1st.danbot1.commands.audio.userphone;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
/**
 * Command to open or close an userphone connection
 * @author Daniel Schmid
 */
@BotCommand({"userphone","vbridge"})
public class CmdUserphone implements Command{

	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.check(event, "userphone");
	}
	
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if (args.length<1) {
			STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"missingArgs"));
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
				UserphoneController.closeConnection(event.getGuild());
			}
			break;
		default:
			break;
		}
	}

	@Override
	public String help() {
		return "userphoneHelp";
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}
	
}
