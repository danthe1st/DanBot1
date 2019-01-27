package commands.utils;

import java.util.List;

import commands.BotCommand;
import commands.Command;
import commands.CommandType;
import core.PermsCore;
import io.github.danthe1st.util.STATIC;
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
		} catch (final Exception e) {
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
					event.getMessage().delete().reason("autodelete").queue();
					
					msgs=history.retrievePast(num).complete();
					int numMsgs=msgs.size();
					try {
						event.getTextChannel().deleteMessages(msgs).queue();
						STATIC.msg(event.getTextChannel(), "Deleted "+numMsgs+" messages", true);
					} catch (final IllegalArgumentException e) {
						STATIC.errmsg(event.getTextChannel(),e.getLocalizedMessage());
					} 
				}
				catch (final Exception e) {
					e.printStackTrace();
				}
			 }
			 else {
				 
				STATIC.errmsg(event.getTextChannel(), "enter a number beetween 2 and 100");
			}
		}
		else {
			STATIC.errmsg(event.getTextChannel(),"Not enough arguments, a number is needed.");
		}
	}
	@Override
	public String help(String prefix) { 
		return "Clears minimal 2 and maximum 100 Messages in the current text Channel(the messages should not be older then 2 weeks\n"
				+ "(see Permission *clearChat* in Command perm get)\n"
				+"*Syntax*: "+prefix+"clear <number of messages>";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.GUILD_MODERATION;
	}
}
