package io.github.danthe1st.danbot1.core;

import java.util.ArrayList;

import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
/**
 * Class to parse a Command into a {@link CommandContainer}
 * @author Daniel Schmid
 */
public class CommandParser {
	/**
	 * zerlegt den Command und f�hrt ihn zu einem <code>CommandContainer</code> zusammen.
	 * method to parse the Command
	 * @param raw the Message Content as String
	 * @param event the {@link MessageReceivedEvent} from the Message
	 * @return the parsed Command
	 */
	public static CommandContainer parser(final String raw, final MessageReceivedEvent event) {
		
		return parser(raw, event, STATIC.getPrefix(event.getGuild()));
	}
	/**
	 * zerlegt den Command und f�hrt ihn zu einem <code>CommandContainer</code> zusammen.
	 * method to parse the Command
	 * @param raw the Message Content as String
	 * @param event the {@link MessageReceivedEvent} from the Message
	 * @param prefix the {@link Guild} prefix
	 * @return the parsed Command
	 */
	public static CommandContainer parser(final String raw, final MessageReceivedEvent event, final String prefix) {
		final String beheaded=raw.replaceFirst(prefix, "");
		final String[] splitBeheaded=beheaded.split(" ");
		final String invoke=splitBeheaded[0];
		final ArrayList<String> split=new ArrayList<String>();
		for (final String s : splitBeheaded) {
			split.add(s);
		}
		final String[] args=new String[split.size()-1];
		
		split.subList(1, split.size()).toArray(args);
		
		
		return new CommandContainer(raw, beheaded, splitBeheaded, invoke, args, event);
	}
	/**
	 * Container for parsed Commands
	 * contains {@link MessageReceivedEvent} and splitted Message Content
	 * @author Daniel Schmid
	 *
	 */
	public static class CommandContainer {

        public final String raw;
        public final String beheaded;
        public final String[] splitBeheaded;
        public final String invoke;
        public final String[] args;
        public final MessageReceivedEvent event;

        public CommandContainer(final String rw, final String beheaded, final String[] splitBeheaded, final String invoke, final String[] args, final MessageReceivedEvent e) {
            this.raw = rw;
            this.beheaded = beheaded;
            this.splitBeheaded = splitBeheaded;
            this.invoke = invoke;
            this.args = args;
            this.event = e;
        }
    }
}
