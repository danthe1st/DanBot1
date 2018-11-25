package commands;

import java.awt.Color;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.LoggerUtils;
import util.STATIC;
/**
 * Interface for Commands<br>
 * @author Daniel Schmid
 */
public interface Command {
	/**
	 * returns if the Command is blocked or something
	 * @param args the Command-Arguments
	 * @param event The {@link MessageReceivedEvent} of the incoming {@link Message}
	 * @return true if Command should be executed, else false
	 */
	public default boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return true;
	}
	/**
	 * The Execution of the Command
	 * @param args the Command-Arguments
	 * @param event The {@link MessageReceivedEvent} of the incoming {@link Message}
	 */
	public void action(String[] args, MessageReceivedEvent event);
	/**
	 * after Command execution
	 * @param success has the command been executed?
	 * @param event The {@link MessageReceivedEvent} of the incoming {@link Message}
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
	 * help for the Command
	 * @param prefix The prefix of the {@link Guild}
	 * @return help String
	 */
	public String help(String prefix);
	/**
	 * returns the Command-type<br>
	 * The Command-Type is used for grouping Commands
	 * @return the Command-type
	 */
	public CommandType getCommandType();
}
