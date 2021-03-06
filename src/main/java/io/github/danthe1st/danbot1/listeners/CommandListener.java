package io.github.danthe1st.danbot1.listeners;

import io.github.danthe1st.danbot1.core.CommandHandler;
import io.github.danthe1st.danbot1.core.CommandParser;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
/**
 * Listener for Commands
 * @author Daniel Schmid
 */
@BotListener
public class CommandListener extends ListenerAdapter {
	/**
	 * if anyone sends a {@link Message} and this Message begins with the Bot prefix for the {@link Guild} it will be parsed and executed
	 * @see CommandParser
	 * @see CommandHandler
	 */
	@Override
	public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
		if(event.getMessage().getContentDisplay().startsWith("--prefix")&&!event.getAuthor().isBot()) {
			CommandHandler.handleCommand(CommandParser.parser( event,"--"));
			return;
		}
		if((event.getMessage().getMentionedUsers().size()==1)&&(event.getMessage().getContentDisplay().startsWith("@"))&&event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfUser())&&!event.getMessage().getAuthor().isBot()) {
			CommandHandler.handleCommand(CommandParser.parser(event,event.getMessage().getContentRaw().split(" ")[0]+" "));
		}
		if(event.getMessage().getContentDisplay().startsWith(STATIC.getPrefix(event.getGuild()))&&(!event.getMessage().getAuthor().isBot())) {
			CommandHandler.handleCommand(CommandParser.parser(event));
		}
		
	}
}
