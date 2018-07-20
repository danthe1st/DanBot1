package commands.utils;

import java.awt.Color;

import commands.Command;
import core.PermsCore;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command f. zuspammen v. Chat
 * @author Daniel Schmid
 *
 */
public class CmdSpam implements Command {
	
	/**
	 * Der Befehl selbst(siehe help)
	 */
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if(!PermsCore.check(event, "spam")) {
			return;
		}
		if (args.length<2) {
			STATIC.errmsg(event.getTextChannel(), help(STATIC.getPrefixExcaped(event.getGuild())));
			return;
		}
		try {
			String spamMsg=args[1];
			for (int i = 2; i < args.length; i++) {
				spamMsg+=" ";
				spamMsg+=args[i];
			}
			int count=Integer.parseInt(args[0]);
			for (int i = 0; i < count; i++) {
				STATIC.msg(event.getTextChannel(), "[spam-Command]["+event.getAuthor().getName()+"] "+spamMsg, Color.black, false);
				
			}
		} catch (NumberFormatException e) {
			STATIC.errmsg(event.getTextChannel(), "Please use a number as argument 1!");
		}
	}
	/**
	 * hilfe: gibt Hilfe zu diesem Command als String zur�ck
	 */
	@Override
	public String help(String prefix) {
		return "Spams a number of messages(ATTENTION: takes a while because of Discord Spam Protection)"
				+ "(see Permission *spam* in Command perm get)\n"
				+"*Syntax*: "+prefix+"spam <number of messages> <message>";
	}
	@Override
	public String getCommandType() {
		return CMD_TYPE_USER;
	}
}
