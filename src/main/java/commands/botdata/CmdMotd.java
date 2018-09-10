package commands.botdata;

import java.util.HashMap;
import java.util.Map;

import commands.Command;
import core.PermsCore;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command for getting/setting a guild-specified Message of the day(standard: invite link for the Bot and the Support Server)
 * @author Daniel Schmid
 *
 */
public class CmdMotd implements Command{

	private static Map<Guild, String> motd=new HashMap<Guild, String>();
	private static final String stdMotd="Invite: https://discordapp.com/api/oauth2/authorize?client_id=371042228891549707&permissions=8&scope=bot\n"
			+ "Support: https://discord.gg/qmwcEjF";
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(!PermsCore.check(event, "motd")) {
			return;
		}	
		String motd=CmdMotd.motd.get(event.getGuild());
		if (motd==null) {
			try {
				CmdMotd.motd.put(event.getGuild(), (String) STATIC.load(event.getGuild().getId()+"/motd.dat"));
				motd=CmdMotd.motd.get(event.getGuild());
			} catch (Exception e) {
				
			}
			if (motd==null) {
				motd=stdMotd;
			}
		}
			
		
		
		if (args.length==0) {
			STATIC.msg(event.getTextChannel(), "*"+motd+"*");
			return;
		}
		
		if(!PermsCore.check(event, "motd.change")) {
			return;
		}
		switch (args[0]) {
		case "reset":
			motd=stdMotd;
			STATIC.save(event.getGuild().getId()+"/motd.dat", motd);
			break;
		case "set":{
			if (args.length==1) {
				STATIC.errmsg(event.getTextChannel(), "not enough arguments");
				return;
			}
			args[0]="";
		}
		default:
			motd="";
			boolean hasElementsBefore=false;
			for (String string : args) {
				if (hasElementsBefore) {
					motd+=" ";
				}
				motd+=string;
				hasElementsBefore=true;
			}
			CmdMotd.motd.put(event.getGuild(), motd);
			STATIC.save(event.getGuild().getId()+"/motd.dat", motd);
			break;
		}
		
	}
	public String help(String prefix) {
		return "displays or sets the message of the day!\n"
				+ "(see Permission *motd(.show/.set)* in Command perm get)\n"
				+"*Syntax*: "+prefix+"motd ((set )<new motd>)";
	}
	@Override
	public String getCommandType() {
		return CMD_TYPE_USER;
	}
}
