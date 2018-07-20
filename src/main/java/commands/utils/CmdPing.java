package commands.utils;


import commands.Command;
import core.PermsCore;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Befehl f. ping(schreibt "Pong!" und gibt Ping zur�ck)
 * @author Daniel Schmid
 *
 */
public class CmdPing implements Command{
	/**
	 * Der Befehl selbst(siehe help)
	 */
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(!PermsCore.check(event, "ping")) {
			return;
		}
		STATIC.msg(event.getTextChannel(), "Pong",true);
		
		STATIC.msg(event.getTextChannel(), "my Ping: "+event.getJDA().getPing());
		
	}

	/**
	 * hilfe: gibt Hilfe zu diesem Command als String zur�ck
	 */
	public String help(String prefix) {
		return "Pong!\n"
				+ "output Bot ping\n"
				+ "(see Permission *ping* in Command perm get)\n"
				+"*Syntax*: "+prefix+"ping";

	}
	@Override
	public String getCommandType() {
		return CMD_TYPE_USER;
	}
}
