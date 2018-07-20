package commands.utils;

import java.util.List;

import commands.Command;
import core.PermsCore;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command um den Chat-Verlauf zu leeren
 * @author Daniel Schmid
 *
 */
public class CmdClear implements Command {

	
	/**
	 * gibt int-Wert aus String (falls m�gl.)
	 * @param str Der String
	 * @return der String als int(oder 0)
	 */
	private int getInt(final String str) {
		try {
			return Integer.parseInt(str);
		} catch (final Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	/**
	 * Der Befehl selbst(siehe help)
	 */
	@Override
	public void action(String[] args, final MessageReceivedEvent event) {
		if(!PermsCore.check(event, "clearChat")) {
			return;
		}
		
		
		
		if(args.length>0) {
			int num=getInt(args[0]);
			
			
			if(num>1&&num<=100) {
				try {
					final MessageHistory history=new MessageHistory(event.getTextChannel());
					List<Message>msgs;
					event.getMessage().delete().queue();
					
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

	
	/**
	 * hilfe: gibt Hilfe zu diesem Command als String zur�ck
	 */
	@Override
	public String help(String prefix) { 
		return "Clears minimal 2 and maximum 100 Messages in the current text Channel(the messages should not be older then 2 weeks\n"
				+ "(see Permission *clearChat* in Command perm get)\n"
				+"*Syntax*: "+prefix+"clear <number of messages>";
	}
	@Override
	public String getCommandType() {
		return CMD_TYPE_GUILD_MODERATION;
	}
}
