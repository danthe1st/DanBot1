package io.github.danthe1st.danbot1.commands.utils;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Ping Command(echos "Pong!" and the current Bot ping)
 * @author Daniel Schmid
 */
@BotCommand("ping")
public class CmdPing implements Command{
	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return PermsCore.check(event, "ping");
	}
	public void action(final String[] args, final GuildMessageReceivedEvent event) {
		if(!PermsCore.check(event, "ping")) {
			return;
		}
		STATIC.msg(event.getChannel(), translate(event.getGuild(),"pingAnswer"),true);
		
		STATIC.msg(event.getChannel(), translate(event.getGuild(),"pingDisplay")+event.getJDA().getGatewayPing());
	}
	public String help() {
		return "pingHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}
}
