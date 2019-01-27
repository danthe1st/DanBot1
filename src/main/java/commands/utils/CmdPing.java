package commands.utils;


import commands.BotCommand;
import commands.Command;
import commands.CommandType;
import core.PermsCore;
import io.github.danthe1st.util.STATIC;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
/**
 * Ping Command(echos "Pong!" and the current Bot ping)
 * @author Daniel Schmid
 */
@BotCommand(aliases = "ping")
public class CmdPing implements Command{
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.check(event, "ping");
	}
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(!PermsCore.check(event, "ping")) {
			return;
		}
		STATIC.msg(event.getTextChannel(), "Pong",true);
		
		STATIC.msg(event.getTextChannel(), "my Ping: "+event.getJDA().getGatewayPing());
	}
	public String help(String prefix) {
		return "Pong!\n"
				+ "output Bot ping\n"
				+ "(see Permission *ping* in Command perm get)\n"
				+"*Syntax*: "+prefix+"ping";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}
}
