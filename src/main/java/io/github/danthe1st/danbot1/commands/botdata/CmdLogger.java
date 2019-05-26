package io.github.danthe1st.danbot1.commands.botdata;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
/**
 * Command to set the Logger-Channel
 * @author Daniel Schmid
 */
@BotCommand(aliases = "cmdlogger")
public class CmdLogger implements Command{
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.check(event, "logger");
	}
	@Override
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(args.length<1) {
			STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(), "missingArgs"));
			return;
		}
		switch (args[0]) {
		case "show":
			if(!PermsCore.check(event, "logger.show")) {
				return;
			}
			STATIC.msg(event.getTextChannel(), translate(event.getGuild(), "showCmdLogChan")+STATIC.getCmdLogger(event.getGuild()));
			
			break;
		case "set":
			if(!PermsCore.check(event, "logger.set")) {
				return;
			}
			if(args.length<2) {
				STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(), "missingArgs"));
				return;
			}
			STATIC.setCmdLogger(event.getGuild(), args[1]);
			break;
		default:
			STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"invalidArgs"));
			break;
		}
	}
	@Override
	public String help() {
		return "cmdLogHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.BOT_MODERATION;
	}
}
