package listeners;

import core.CommandHandler;
import core.CommandParser;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.STATIC;
/**
 * Listener for Commands
 * @author Daniel Schmid
 */
public class CommandListener extends ListenerAdapter {
	/**
	 * if anyone sends a {@link Message} and this Message begins with the Bot prefix for the {@link Guild} it will be parsed and executed
	 * @see CommandParser
	 * @see CommandHandler
	 */
	
	public void onMessageReceived(final MessageReceivedEvent event) {
		
		if(event.getGuild()==null) {
			return;
		}
		
		if(event.getMessage().getContentDisplay().startsWith("--prefix")&&!event.getAuthor().isBot()) {
			CommandHandler.handleCommand(CommandParser.parser(event.getMessage().getContentRaw(), event,"--"));
			return;
		}
		if((event.getMessage().getMentionedUsers().size()==1)&&(event.getMessage().getContentDisplay().startsWith("@"))&&event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfUser())&&!event.getMessage().getAuthor().isBot()) {
			CommandHandler.handleCommand(CommandParser.parser(event.getMessage().getContentRaw(), event,event.getMessage().getContentRaw().split(" ")[0]+" "));
		}
		//System.out.println(event.getAuthor().getName()+": "+event.getMessage().getContent());
		if(event.getMessage().getContentDisplay().startsWith(STATIC.getPrefix(event.getGuild()))&&(!event.getMessage().getAuthor().isBot())) {
			CommandHandler.handleCommand(CommandParser.parser(event.getMessage().getContentRaw(), event));
		}
		
	}
}
