package core;

import java.util.ArrayList;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * zerlegt den Command und führt ihn zu einem <code>CommandContainer</code> zusammen.
 * @author Daniel Schmid
 *
 */

public class CommandParser {
	/**
	 * zerlegt den Command und führt ihn zu einem <code>CommandContainer</code> zusammen.
	 * @param raw der Cammand selbst
	 * @param event das <code>MessageRecievedExent</code> von Discord
	 * @return der Command als CommandContainer
	 */
	public static CommandContainer parser(final String raw, final MessageReceivedEvent event) {
		
		return parser(raw, event, STATIC.getPrefix(event.getGuild()));
	}
	/**
	 * zerlegt den Command und führt ihn zu einem <code>CommandContainer</code> zusammen.
	 * @param raw der Cammand selbst
	 * @param event das <code>MessageRecievedExent</code> von Discord
	 * @param prefix Der Command-Prefix
	 * @return der Command als CommandContainer
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
	 * Container f. Command<br>
	 * enthält einzelne Command-Teile
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
