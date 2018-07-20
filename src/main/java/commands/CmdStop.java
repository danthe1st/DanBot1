package commands;

import java.awt.Color;

import core.Main;
import core.PermsCore;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command um den Bot zu stoppen(sofort)
 * @author Daniel Schmid
 */
public class CmdStop implements Command{
	/**
	 * Der Befehl selbst(siehe help)
	 */
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

	
	/**
	 * hilfe: gibt Hilfe zu diesem Command als String zur�ck
	 */
	@Override
	public String help(String prefix) {
		return "Emergancy-stopps the Bot\n"
				+ "(see Permission *stop* in Command perm get)\n"
				+"*Syntax*: "+prefix+"stop";
	}
	@Override
	public String getCommandType() {
		return CMD_TYPE_BOT_MODERATION;
	}
}
