package commands.botadmin;

import commands.Command;
import listeners.GuildChangeListener;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;

public class CmdReload implements Command {

	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if (!event.getAuthor().getId().equals("358291050957111296")) {
			STATIC.errmsg(event.getTextChannel(), "This command can be *only* used by the developer of this Bot!");
			return;
		}
		STATIC.loadData(event.getJDA());
		for (Guild guild : event.getJDA().getGuilds()) {
			GuildChangeListener.saveGuildData(guild);
		}
	}

	@Override
	public String help(String prefix) {
		return "reloads Guild Files";
	}

	@Override
	public String getCommandType() {
		return CMD_TYPE_ADMIN;
	}

}
