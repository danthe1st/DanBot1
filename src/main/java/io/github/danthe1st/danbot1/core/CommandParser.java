package io.github.danthe1st.danbot1.core;

import java.util.ArrayList;
import java.util.regex.Pattern;

import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
/**
 * Class to parse a Command into a {@link CommandContainer}
 * @author Daniel Schmid
 */
public class CommandParser {
	
	private CommandParser(){
		//prevent instantiation
	}
	/**
	 * parses the command to a <code>CommandContainer</code>
	 * @param event the {@link GuildMessageReceivedEvent} from the Message
	 * @return the parsed Command
	 */
	public static CommandContainer parser(final GuildMessageReceivedEvent event) {
		
		return parser(event, STATIC.getPrefix(event.getGuild()));
	}
	/**
	 * parses the command to a <code>CommandContainer</code>
	 * @param event the {@link GuildMessageReceivedEvent} from the Message
	 * @param prefix the {@link Guild} prefix
	 * @return the parsed Command
	 */
	public static CommandContainer parser(final GuildMessageReceivedEvent event, final String prefix) {
		final String raw=event.getMessage().getContentRaw();
		final String beheaded=raw.replaceFirst(Pattern.quote(prefix), "");
		final String[] splitBeheaded=beheaded.split(" ");
		final String invoke=splitBeheaded[0];
		final ArrayList<String> split=new ArrayList<>();
		boolean inQuoute=false;
		for (String s : splitBeheaded) {
			if (inQuoute) {
				split.add(split.remove(split.size()-1).concat(" ").concat(s.substring(0,s.length()-1)));
				if (s.endsWith("\"")) {
					inQuoute=false;
				}
			}else {
				if (s.startsWith("\"")&&!s.endsWith("\"")) {
					inQuoute=true;
					s=s.substring(1);
				}
				split.add(s);
			}
		}
		final String[] args=new String[split.size()-1];
		split.subList(1, split.size()).toArray(args);
		return new CommandContainer(invoke, args, event);
	}
	/**
	 * Container for parsed Commands
	 * contains {@link GuildMessageReceivedEvent} and splitted Message Content
	 * @author Daniel Schmid
	 *
	 */
	public static class CommandContainer {

        public final String invoke;
        public final String[] args;
        public final GuildMessageReceivedEvent event;

        public CommandContainer(final String invoke, final String[] args, final GuildMessageReceivedEvent e) {
            this.invoke = invoke;
            this.args = args;
            this.event = e;
        }
    }
}
