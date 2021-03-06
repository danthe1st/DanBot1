package io.github.danthe1st.danbot1.commands.botdata;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.util.HashMap;
import java.util.Map;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Command for getting/setting a guild-specified Message of the day(standard: invite link for the Bot and the Support Server)
 * @author Daniel Schmid
 */
@BotCommand("motd")
public class CmdMotd implements Command{
	private static final String MOTD_FILENAME="/motd.dat";
	private static Map<Guild, String> motd=new HashMap<>();
	private static final String STD_MOTD="stdMOTD";
	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return PermsCore.check(event, "motd");
	}
	public void action(final String[] args, final GuildMessageReceivedEvent event) {
		if(!PermsCore.check(event, "motd")) {
			return;
		}	
		String motd=CmdMotd.motd.get(event.getGuild());
		if (motd==null) {
			try {
				CmdMotd.motd.put(event.getGuild(), (String) STATIC.load(event.getGuild().getId()+MOTD_FILENAME));
				motd=CmdMotd.motd.get(event.getGuild());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (motd==null) {
				motd=translate(event.getGuild(),STD_MOTD);
			}
		}
			
		
		
		if (args.length==0) {
			STATIC.msg(event.getChannel(), "*"+motd+"*");
			return;
		}
		
		if(!PermsCore.check(event, "motd.change")) {
			return;
		}
		switch (args[0]) {
		case "reset":
			motd=translate(event.getGuild(),STD_MOTD);
			STATIC.save(event.getGuild().getId()+MOTD_FILENAME, motd);
			break;
		case "set":{
			if (args.length==1) {
				STATIC.errmsg(event.getChannel(), "not enough arguments");
				return;
			}
			args[0]="";
			break;
		}
		default:
			StringBuilder motdBuilder=new StringBuilder();
			boolean hasElementsBefore=false;
			for (String string : args) {
				if (hasElementsBefore) {
					motdBuilder.append(" ");
				}
				motdBuilder.append(string);
				hasElementsBefore=true;
			}
			motd=motdBuilder.toString();
			CmdMotd.motd.put(event.getGuild(), motd);
			STATIC.save(event.getGuild().getId()+MOTD_FILENAME, motd);
			break;
		}
		
	}
	public String help() {
		return "motdHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}
}
