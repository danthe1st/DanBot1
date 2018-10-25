package commands.moderation.nospam;

import commands.Command;
import commands.CommandType;
import core.PermsCore;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command-Class for the spam-protection
 * WORK IN PROCESS
 * TODO: UI, Storage
 * @author Daniel Schmid
 */
public class CmdNoSpam implements Command{

	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if (args.length==0||args[0].equalsIgnoreCase("show")) {
			if (!PermsCore.check(event, "nospam.see")) {
				return;
			}
			//TODO show spam protection
			
			if (!SpamProtectionContainer.isGuildProtected(event.getGuild())) {
				STATIC.msg(event.getTextChannel(), "This Guild has no spam-protection.");
				return;
			}
			SpamProtectionContainer container=SpamProtectionContainer.getSpamContainer(event.getGuild());
			
			STATIC.msg(event.getTextChannel(), "action: "+container.getType()+"\n"
					+"tries: "+container.getTries()+"\n"
							+ "time: "+container.getTime());
			return;
		}
		switch (args[0].toLowerCase()) {
		case "add":
		case "+":
			if (!PermsCore.check(event, "nospam.change")) {
				return;
			}
			if (args.length<3) {
				STATIC.errmsg(event.getTextChannel(), "too few arguments.");
			}
			SpamProtectType punishType=null;
			switch (args[1].toLowerCase()) {
			case "delete":
			case "del":
				punishType=SpamProtectType.delete;
				break;
			case "kick":
				punishType=SpamProtectType.kick;
				break;
			case "ban":
				punishType=SpamProtectType.ban;
				break;
			default:
				break;
			}
			int tries=0;
			try {
				tries=Integer.parseInt(args[2]);
			} catch (Exception e) {
				STATIC.errmsg(event.getTextChannel(), "Incorrect argument: "+args[2]+"\n This has to be a number!");
			}
			int time=0;
			try {
				time=Integer.parseInt(args[3]);
			} catch (Exception e) {
				STATIC.errmsg(event.getTextChannel(), "Incorrect argument: "+args[3]+"\n This has to be a number!");
			}
			
			//TODO testing
			SpamProtectionContainer.addSpamContainer(event.getGuild(), punishType, tries-1, time*1000);
			break;
		case "rem":
		case "remove":
		case "delete":
			SpamProtectionContainer.removeSpamContainer(event.getGuild());
			break;
		default:
			STATIC.errmsg(event.getTextChannel(), "Syntax Error - please type in a valid subcommand");
			break;
		}
	}

	@Override
	public String help(String prefix) {
		return "controls the spam protection\n"
				+ "***WORK IN PROCESS***";
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.BOT_MODERATION;
	}
	
}
