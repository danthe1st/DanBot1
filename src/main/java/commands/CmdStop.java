package commands;

import java.awt.Color;

import core.Main;
import core.PermsCore;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command to stop the Bot
 * @author Daniel Schmid
 */
@BotCommand(aliases = "stop")
public class CmdStop implements Command{//TODO make it only possible for specified Guilds
	@Override
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(!PermsCore.check(event, "stop")) {
			return;
		}
		else if(!Main.getStopable()) {
			STATIC.errmsg(event.getTextChannel(), "Bot not stoppable!");
			return;
		}
		STATIC.msg(event.getTextChannel(), "DanBot1 "+STATIC.VERSION +" stopped.",Color.ORANGE,false);
		System.out.println("stopped by "+event.getAuthor());
		event.getJDA().shutdown();
		System.exit(0);
	}
	@Override
	public String help(String prefix) {
		return "Emergancy-stopps the Bot\n"
				+ "(see Permission *stop* in Command perm get)\n"
				+"*Syntax*: "+prefix+"stop";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.BOT_MODERATION;
	}
}
