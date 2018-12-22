package commands.moderation.nospam;

import commands.BotCommand;
import commands.Command;
import commands.CommandType;
import core.PermsCore;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command-Class for the spam-protection
 * @author Daniel Schmid
 */
@BotCommand(aliases = "nospam")
public class CmdNoSpam implements Command{

	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if (args.length==0||args[0].equalsIgnoreCase("show")) {
			if (!PermsCore.check(event, "nospam.see")) {
				return;
			}
			if (!SpamProtectionContainer.isGuildProtected(event.getGuild())) {
				STATIC.msg(event.getTextChannel(), "This Guild has no spam-protection.");
				return;
			}
			SpamProtectionContainer container=SpamProtectionContainer.getSpamContainer(event.getGuild());
			
			STATIC.msg(event.getTextChannel(), "action: "+container.getType()+"\n"
					+"tries: "+(container.getTries()+1)+"\n"
							+ "time: "+container.getTime()/1000+"s");
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
			SpamProtectionContainer.addSpamContainer(event.getGuild(), punishType, tries-1, time*1000);
			break;
		case "rem":
		case "remove":
		case "delete":
		case "-":
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
				+ "setup/change spam protection: add\n"
				+ "\taction can be delete(only delete messages), kick(kicks the Spammer) or ban(bans the spammer)\n"
				+ "delete spam protection: remove\n"
				+ "(see Permission *nospam.see* and *nospam.change* in Command perm get)\n"
				+ "*Syntax*: \"+nospam+\"[add <action> <tries> <time>, remove]";
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.GUILD_MODERATION;
	}
	
}
