package commands.utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import commands.BotCommand;
import commands.Command;
import commands.CommandType;
import core.CommandHandler;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * prints help for all Commands
 * @author Daniel Schmid
 */
@BotCommand(alias = "help")
public class CmdHelp implements Command{
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		Map<CommandType, Map<String,Command>> commandTypes=new HashMap<>();
		CommandHandler.commands.forEach((k,v)->{
			if (!commandTypes.containsKey(v.getCommandType())) {
				commandTypes.put(v.getCommandType(), new HashMap<>());
			}
			commandTypes.get(v.getCommandType()).put(k,v);
		});
		EmbedBuilder eb=new EmbedBuilder();
		eb.setDescription("\t**Bot Help**\n"
						+ "for a complete Description visit *https://www.wwwmaster.at/daniel/data/DanBot1/*\n\n");
		commandTypes.forEach((typeName,commands)->{
			eb.appendDescription("`"+typeName+"`:\n")
			.setColor(Color.GREEN);
			
			commands.forEach((name,command)->{
				EmbedBuilder backUp=new EmbedBuilder(eb);
				String help="**"+STATIC.getPrefix(event.getGuild()) +name+"**:\n"+command.help(STATIC.getPrefixExcaped(event.getGuild()))+"\n\n";
				try {
					eb.appendDescription(help);
					if (!(eb.isValidLength(AccountType.CLIENT)/*&&eb.isValidLength(AccountType.BOT)*/)) {
						event.getAuthor().openPrivateChannel().complete().sendMessage(backUp.build()).queue();
						eb.clear();
						eb.appendDescription(help);
					}	
				} catch (IllegalArgumentException e) {
					event.getAuthor().openPrivateChannel().complete().sendMessage(backUp.build()).queue();
					eb.clear();
					eb.appendDescription(help);
				}
				
				
			});
			event.getAuthor().openPrivateChannel().complete().sendMessage(eb.build()).queue();
			
			eb.clear();
		});
	}
	@Override
	public String help(String prefix) {
		return "Show this help\n"
				+"*Syntax*: "+prefix+"help";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}
}
