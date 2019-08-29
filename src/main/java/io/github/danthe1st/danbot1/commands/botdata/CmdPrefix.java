package io.github.danthe1st.danbot1.commands.botdata;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Command to change/show the Bot prefix
 * @author Daniel Schmid
 */
@BotCommand("prefix")
public class CmdPrefix implements Command{
	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return PermsCore.check(event, "prefix");
	}
	@Override
	public void action(final String[] args, final GuildMessageReceivedEvent event) {
		if(args.length<1) {
			STATIC.errmsg(event.getChannel(), translate(event.getGuild(),"missingArgs"));
			return;
		}
		switch (args[0]) {
		case "show":
			if(!PermsCore.check(event, "prefix.show")) {
				return;
			}
			STATIC.msg(event.getChannel(), STATIC.getPrefix(event.getGuild()));
			break;
		case "set":
			if(!PermsCore.check(event, "prefix.set")) {
				return;
			}
			if(args.length<2) {
				STATIC.errmsg(event.getChannel(), translate(event.getGuild(),"missingArgs"));
				return;
			}
			STATIC.setPrefix(event.getGuild(),args[1]);
			break;
		default:
			STATIC.errmsg(event.getChannel(), translate(event.getGuild(),"invalidArgs"));
			break;
		}
	}
	@Override
	public String help() {
		return "prefixHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.BOT_MODERATION;
	}
}
