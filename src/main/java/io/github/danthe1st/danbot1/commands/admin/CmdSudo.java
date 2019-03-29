package io.github.danthe1st.danbot1.commands.admin;
import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.CommandHandler;
import io.github.danthe1st.danbot1.core.CommandParser;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
/**
 * Command for executing Commands as another user
 * @author Daniel Schmid
 */
@BotCommand(aliases = "sudo")
public class CmdSudo implements Command{
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.checkOwner(event);
	}
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if (args.length<2) {
			STATIC.errmsg(event.getTextChannel(), "not anough arguments!");
			return;
		}
		Member user=null;
		try {
			user=event.getGuild().getMemberById(args[0]);
		} catch (NumberFormatException e) {
			
		}
		if (user==null) {
			user=event.getGuild().getMembersByName(args[0], true).get(0);
		}
		if (user==null) {
			user=event.getGuild().getMembersByNickname(args[0], true).get(0);
		}
		if (user==null) {
			STATIC.errmsg(event.getTextChannel(), "Invalid user");
		}
		
		
		String toReplace=event.getMessage().getContentRaw().substring(0,event.getMessage().getContentRaw().indexOf(args[1]));
		String raw=event.getMessage().getContentRaw().replaceAll(toReplace, STATIC.getPrefix(event.getGuild()));
		String display=event.getMessage().getContentDisplay().replaceAll(toReplace, STATIC.getPrefix(event.getGuild()));
		String stripped=event.getMessage().getContentRaw().replaceAll(toReplace, STATIC.getPrefix(event.getGuild()));
		SudoMessage msg=new SudoMessage(event.getMessage(),raw, display, stripped,user);
			
		MessageReceivedEvent sudoEvent=new MessageReceivedEvent(event.getJDA(), event.getResponseNumber(), msg);
		CommandHandler.handleCommand(CommandParser.parser(sudoEvent, STATIC.getPrefix(event.getGuild())));
	}

	@Override
	public String help(String prefix) {
		return "executes Commands as another User\n"
				+ "**CAN ONLY BE USED BY *the bot-admin***";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.ADMIN;
	}
}
