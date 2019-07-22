package io.github.danthe1st.danbot1.commands.utils;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.util.List;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
/**
 * Clears history of private Messages (min 2, max 100 Messages)
 * @author Daniel Schmid
 */
@BotCommand("clearpm")
public class CmdClearPMs implements Command {
	private int getInt(final String str) {
		try {
			return Integer.parseInt(str);
		} catch (final Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if(args.length>0) {
			int num=getInt(args[0]);
			if(num>1&&num<=100) {
				try {
					final MessageHistory history=new MessageHistory(event.getAuthor().openPrivateChannel().complete());
					List<Message>msgs;
					
					msgs=history.retrievePast(num).complete();
					int numMsgs=0;
					for (Message message : msgs) {
						message.delete().reason(translate(event.getGuild(),"ClearPMReason")).queue();
						numMsgs++;
					}
					STATIC.msg(event.getTextChannel(), String.format(translate(event.getGuild(),"MsgsDeleted"),numMsgs), true);
				}
				catch (final Exception e) {
					e.printStackTrace();
				}
			 }
			 else {
				STATIC.errmsg(event.getTextChannel(), String.format(translate(event.getGuild(),"needIntInRange"),2,100));
			 }
		}
		else {
			STATIC.errmsg(event.getTextChannel(),translate(event.getGuild(),"missingArgs"));
		}
	}

	@Override
	public String help() {
		return "clearPMHelp";
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}

}
