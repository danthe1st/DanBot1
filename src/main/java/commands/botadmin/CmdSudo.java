package commands.botadmin;
import commands.Command;
import core.CommandHandler;
import core.CommandParser;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command for executing Commands as another user
 * @author Daniel Schmid
 */
public class CmdSudo implements Command{

	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if (!event.getAuthor().getId().equals("358291050957111296")) {
			STATIC.errmsg(event.getTextChannel(), "This command can be *only* used by the developer of this Bot!");
			return;
		}
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
		CommandHandler.handleCommand(CommandParser.parser(sudoEvent.getMessage().getContentRaw(), sudoEvent, STATIC.getPrefix(event.getGuild())));
	}

	@Override
	public String help(String prefix) {
		return "executes Commands as another User\n"
				+ "**CAN ONLY BE USED BY *Daniel Schmid***";
	}
	@Override
	public String getCommandType() {
		return CMD_TYPE_ADMIN;
	}
}
