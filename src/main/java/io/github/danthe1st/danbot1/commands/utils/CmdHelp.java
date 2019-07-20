package io.github.danthe1st.danbot1.commands.utils;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.awt.Color;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.CommandHandler;
import io.github.danthe1st.danbot1.util.LanguageController;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * prints help for all Commands
 * @author Daniel Schmid
 */
@BotCommand("help")
public class CmdHelp implements Command{
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if (args.length==0) {
			sendListOfCommands(event.getGuild(), event.getAuthor().openPrivateChannel().complete());
		}
		else {
			for (CommandType type: CommandType.values()) {
				if (args[0].replaceAll("[ _]", "").equalsIgnoreCase(type.name().replaceAll("[ _]", ""))||
						args[0].replaceAll("[ _]", "").equalsIgnoreCase(translate(event.getGuild(), "cmdType_"+type.name()).replaceAll("[ _]", ""))) {
					sendHelpOfCommandType(event.getGuild(), event.getAuthor().openPrivateChannel().complete(), type);
					return;
				}
			}
			if (CommandHandler.getCommands().containsKey(args[0].toLowerCase())) {
				sendHelpOfCommand(event.getGuild(), event.getChannel(), args[0]);
			}
		}
	}
	private void sendHelpOfCommand(Guild g,MessageChannel chan,String cmdName) {
		EmbedBuilder eb=new EmbedBuilder();
		eb.setDescription(translate(g,"helpTitle"));
		String help="**"+STATIC.getPrefix(g) +cmdName+"**:\n"+LanguageController.translate(g, CommandHandler.getCommands().get(cmdName.toLowerCase()).help()).replace("--",STATIC.getPrefixEscaped(g))+"\n\n";
		eb.appendDescription(help);
		chan.sendMessage(eb.build()).queue();
	}
	private void sendHelpOfCommandType(Guild g,MessageChannel chan,CommandType type) {
		if (type==null) {
			throw new NullPointerException();
		}
		Map<String,Command> commands=new HashMap<>();
		CommandHandler.getCommands().forEach((k,v)->{
			if(type.equals(v.getCommandType())) {
				commands.put(k, v);
			}
		});
		EmbedBuilder eb=new EmbedBuilder();
		eb.setDescription(translate(g,"helpTitle"));
		eb.appendDescription("`"+translate(g,"cmdType_"+type.name())+"`:\n").setColor(Color.GREEN);
		commands.forEach((name,command)->{
			if (command.help()!=null) {
				EmbedBuilder backUp=new EmbedBuilder(eb);
				String help="**"+STATIC.getPrefix(g) +name+"**:\n"+LanguageController.translate(g, command.help()).replace("--",STATIC.getPrefixEscaped(g))+"\n\n";
				try {
					eb.appendDescription(help);
					if (!(eb.isValidLength(AccountType.CLIENT)/*&&eb.isValidLength(AccountType.BOT)*/)) {
						chan.sendMessage(backUp.build()).queue();
						eb.clear();
						eb.appendDescription(help);
					}	
				} catch (IllegalArgumentException e) {
					chan.sendMessage(backUp.build()).queue();
					eb.clear();
					eb.appendDescription(help);
				}
			}
		});
		if (!eb.isEmpty()) {
			chan.sendMessage(eb.build()).queue();
			eb.clear();
		}
	}
	private void sendListOfCommands(Guild g,MessageChannel chan) {
		Map<CommandType, Map<String,Command>> commandTypes=new EnumMap<>(CommandType.class);
		CommandHandler.getCommands().forEach((k,v)->{
			if (!commandTypes.containsKey(v.getCommandType())) {
				commandTypes.put(v.getCommandType(), new HashMap<>());
			}
			commandTypes.get(v.getCommandType()).put(k,v);
		});
		EmbedBuilder eb=new EmbedBuilder();
		eb.setDescription(translate(g,"cmdListTitle"));
		commandTypes.forEach((commandType,commands)->{
			if (commandType!=null) {
				eb.appendDescription("\n`"+translate(g,"cmdType_"+commandType.name())+"`:\n").setColor(Color.GREEN);
			}else {
				eb.appendDescription("\n"+translate(g,"cmdWithoutType")).setColor(Color.GREEN);
			}
			commands.forEach((name,command)->{
				if (commandType!=null&&command.help()!=null) {
					eb.appendDescription(STATIC.getPrefix(g) +name+" ");
				}
			});
		});
		if (!eb.isEmpty()) {
			chan.sendMessage(eb.build()).queue();
			eb.clear();
		}
	}
	@Override
	public String help() {
		return "helpHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}
}
