package io.github.danthe1st.danbot1.commands.admin;
import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.CommandHandler;
import io.github.danthe1st.danbot1.core.CommandParser;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Command for executing Commands as another user
 * @author Daniel Schmid
 */
@BotCommand("sudo")
public class CmdSudo implements Command{
	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return PermsCore.checkOwner(event);
	}
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if (args.length<2) {
			STATIC.errmsg(event.getChannel(), translate(event.getGuild(),"missingArgs"));
			return;
		}
		Member user=null;
		try {
			user=event.getGuild().getMemberById(args[0]);
		} catch (NumberFormatException e) {
			//ignore
		}
		if (user==null) {
			user=event.getGuild().getMembersByName(args[0], true).get(0);
		}
		if (user==null) {
			user=event.getGuild().getMembersByNickname(args[0], true).get(0);
		}
		if (user==null) {
			STATIC.errmsg(event.getChannel(), "Invalid user");
		}
		
		
		String toReplace=event.getMessage().getContentRaw().substring(0,event.getMessage().getContentRaw().indexOf(args[1]));
		String raw=event.getMessage().getContentRaw().replaceAll(toReplace, STATIC.getPrefix(event.getGuild()));
		String display=event.getMessage().getContentDisplay().replaceAll(toReplace, STATIC.getPrefix(event.getGuild()));
		String stripped=event.getMessage().getContentRaw().replaceAll(toReplace, STATIC.getPrefix(event.getGuild()));
		SudoMessage msg=new SudoMessage(event.getMessage(),raw, display, stripped,user);
			
		GuildMessageReceivedEvent sudoEvent=new GuildMessageReceivedEvent(event.getJDA(), event.getResponseNumber(), msg);
		CommandHandler.handleCommand(CommandParser.parser(sudoEvent, STATIC.getPrefix(event.getGuild())));
	}

	@Override
	public String help() {
		return "sudoHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.ADMIN;
	}
}
