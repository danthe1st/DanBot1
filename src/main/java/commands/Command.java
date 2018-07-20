package commands;

import java.awt.Color;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.LoggerUtils;
import util.STATIC;
/**
 * Interface f. Command<br>
 * @author Daniel Schmid
 *
 */
public interface Command {
	/**
	 * false wenn Command ausgef�hrt werden soll
	 * @param args Argumente des Commands
	 * @param event Event der Empfangenen Command-Nachricht
	 * @return false wenn ausgef�hrt werden darf
	 */
	public default boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return true;
	}
	/**
	 * Der Command selbst
	 * @param args Argumente des Commands
	 * @param event Event der Empfangenen Command-Nachricht
	 */
	public void action(String[] args, MessageReceivedEvent event);
	/**
	 * Nach Ausf�hrung des Commands
	 * @param success wurde der Command ausgef�hrt(boolean)
	 * @param event Event der Empfangenen Command-Nachricht
	 */
	public default void executed(boolean success, MessageReceivedEvent event) {
		
		String s="["+event.getGuild().getName()+"] Command \""+event.getMessage().getContentDisplay()+"\" was";
		if (success) {
			s+=" successfully";
		}
		s+=" executed by \""+event.getAuthor().getName()+"\" in channel \""+event.getTextChannel().getName()+"\".";
		System.out.println(s);
		if (!event.getGuild().getTextChannelsByName(STATIC.getCmdLogger(event.getGuild()), true).isEmpty()) {
			try {
				event.getGuild().getTextChannelsByName(STATIC.getCmdLogger(event.getGuild()), true).get(0).sendMessage(new EmbedBuilder()
						.setColor(Color.GRAY)
						.setDescription(s)
						.build()).queue();
			} catch (net.dv8tion.jda.core.exceptions.InsufficientPermissionException e) {
			}
			
		}
		
		LoggerUtils.logCommand(event);
	}
	/**
	 * Hilfe zu einem Befehl
	 * @param prefix Der Prefix der Commands in dieser Guild
	 * @return Hilfe
	 */
	public String help(String prefix);
	/**
	 * gibt den Command-Type des Commands zurück<br>
	 * dieser wird für eine Gruppierung von Commands verwendet
	 * @return der Command Type
	 */
	public String getCommandType();
	
	//Command-Types
	public static final String CMD_TYPE_ADMIN="Admin Command - only dan1st";
	public static final String CMD_TYPE_BOT_MODERATION="DanBot1 Moderation Command";
	public static final String CMD_TYPE_USER="User Command";
	public static final String CMD_TYPE_GUILD_MODERATION="Discord Server Moderation Command";
}
