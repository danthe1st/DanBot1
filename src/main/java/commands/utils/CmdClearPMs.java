package commands.utils;

import java.util.List;

import commands.Command;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;

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
					//event.getMessage().delete().queue();
					
					msgs=history.retrievePast(num).complete();
					int numMsgs=0;
					for (Message message : msgs) {
						try {
							message.delete().queue();
							numMsgs++;
						} catch (Exception e) {
						}
						
					}
					STATIC.msg(event.getTextChannel(), "Deleting "+numMsgs+" messages", true);
				}
				catch (final Exception e) {
					e.printStackTrace();
				}
			 }
			 else {
				 
				STATIC.errmsg(event.getTextChannel(), "enter a number beetween 2 and 100");
			 }
		}
		//else {
			//STATIC.errmsg(event.getTextChannel(),"Not enough arguments, a number is needed.");
		//}
	}

	@Override
	public String help(String prefix) {
		return "Clears minimal 2 and maximum 100 Messages in private Channel with the Bot\n"
				+"*Syntax*: "+prefix+"clearPM <number of messages>";
	}

	@Override
	public String getCommandType() {
		return CMD_TYPE_USER;
	}

}
