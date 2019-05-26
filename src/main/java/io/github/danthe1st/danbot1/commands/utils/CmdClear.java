package io.github.danthe1st.danbot1.commands.utils;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.util.List;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Command to delete the Chat-History (min 2, max 100 Messages)
 * @author Daniel Schmid 
 */
@BotCommand(aliases = {"cls","clear"})
public class CmdClear implements Command {
	/**
	 * parses {@link String} to int
	 * @param str Der String to be parsed
	 * @return the String as int(or 0 if parsing failed)
	 */
	private int getInt(final String str) {
		try {
			return Integer.parseInt(str);
		} catch (final NumberFormatException e) {
			return 0;
		}
	}
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.check(event, "clearChat");
	}
	@Override
	public void action(String[] args, final MessageReceivedEvent event) {
		if(args.length>0) {
			int num=getInt(args[0]);
			if(num>1&&num<=100) {
				try {
					final MessageHistory history=new MessageHistory(event.getTextChannel());
					List<Message>msgs;
					event.getMessage().delete().reason(translate(event.getGuild(),"ClearReason")).queue();
					
					msgs=history.retrievePast(num).complete();
					int numMsgs=msgs.size();
					try {
						event.getTextChannel().deleteMessages(msgs).queue();
						STATIC.msg(event.getTextChannel(), String.format(translate(event.getGuild(),"MsgsDeleted"),numMsgs), true);
					} catch (final IllegalArgumentException e) {
						STATIC.errmsg(event.getTextChannel(),e.getLocalizedMessage());
					} 
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
		return "clearHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.GUILD_MODERATION;
	}
}
