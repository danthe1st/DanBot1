package io.github.danthe1st.danbot1.commands.utils.spam;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Command to spam messages
 * @author Daniel Schmid
 */
@BotCommand("spam")
public class CmdSpam implements Command {
	private static final int MAX_MSG_NUMBER_PER_SPAM=20;
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.check(event, "spam");
	}
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if (args.length==1&&args[0].equals("0")) {
			MsgSpammer.addMsgSpam(0, event.getTextChannel(), null,null);
			return;
		}
		if (args.length<1) {
			STATIC.errmsg(event.getTextChannel(), help().replace("--",STATIC.getPrefixEscaped(event.getGuild())));
			return;
		}
		try {
			int count=Integer.parseInt(args[0]);
			if (count!=0&&args.length<2) {
				STATIC.errmsg(event.getTextChannel(), help().replace("--",STATIC.getPrefixEscaped(event.getGuild())));
				return;
			}
			StringBuilder spamMsg=new StringBuilder(args[1]);
			for (int i = 2; i < args.length; i++) {
				spamMsg.append(" ");
				spamMsg.append(args[i]);
			}
			
			if (count>MAX_MSG_NUMBER_PER_SPAM) {
				STATIC.errmsg(event.getTextChannel(), String.format(translate(event.getGuild(),"spamLimitExceed"),MAX_MSG_NUMBER_PER_SPAM));
				return;
			}
			MsgSpammer.addMsgSpam(count, event.getTextChannel(), spamMsg.toString(),event.getAuthor());
		} catch (NumberFormatException e) {
			STATIC.errmsg(event.getTextChannel(), String.format(translate(event.getGuild(),"errArgNoInt"), 1));
		}
	}
	@Override
	public String help() {
		return "spamHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}
}
