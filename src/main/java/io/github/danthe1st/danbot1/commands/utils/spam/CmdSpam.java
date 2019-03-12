package io.github.danthe1st.danbot1.commands.utils.spam;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
/**
 * Command to spam a Message
 * @author Daniel Schmid
 */
@BotCommand(aliases = "spam")
public class CmdSpam implements Command {
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
			STATIC.errmsg(event.getTextChannel(), help(STATIC.getPrefixEscaped(event.getGuild())));
			return;
		}
		try {
			String spamMsg=args[1];
			for (int i = 2; i < args.length; i++) {
				spamMsg+=" ";
				spamMsg+=args[i];
			}
			int count=Integer.parseInt(args[0]);
			if (count>100) {
				STATIC.errmsg(event.getTextChannel(), "Spamming more than 100 Messages at once is not allowed.");
			}
			MsgSpammer.addMsgSpam(count, event.getTextChannel(), spamMsg,event.getAuthor());
		} catch (NumberFormatException e) {
			STATIC.errmsg(event.getTextChannel(), "Please use a number as argument 1!");
		}
	}
	@Override
	public String help(String prefix) {
		return "Spams a number of messages (ATTENTION: takes a while because of Discord Spam Protection)\n"
				+ "the spam can be stopped with "+prefix+"spam 0\n"
				+ "(see Permission *spam* in Command perm get)\n"
				+"*Syntax*: "+prefix+"spam <number of messages> <message>";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}
}
