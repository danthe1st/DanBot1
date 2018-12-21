package commands.admin;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import commands.Command;
import commands.CommandType;
import core.BotCommand;
import core.PermsCore;
/**
 * Command to Evaluate Code
 * @author Daniel Schmid
 */
@BotCommand(aliases = "eval")
public class CmdEval implements Command{
	private ScriptEngine se;
	
	public CmdEval() {
		se = new ScriptEngineManager().getEngineByName("Nashorn");
		se.put("System", System.class);
        try {
			se.eval("System=System.static");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.checkOwner(event);	
	}
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
        se.put("event", event);
        se.put("jda", event.getJDA());
        se.put("guild", event.getGuild());
        se.put("channel", event.getChannel());
       
        String script="";
        for (String string : args) {
			script+=string+" ";
		}
        try {
			Object ergebnis=se.eval(script);
			if (ergebnis != null) {
				STATIC.msg(event.getTextChannel(), ergebnis.toString());
				
			}
			
		} catch (ScriptException e) {
			STATIC.errmsg(event.getTextChannel(), "Sorry, it didn't work\n"+e.getMessage());
		} catch (Exception e) {
			STATIC.errmsg(event.getTextChannel(), "An unknown Error occured\n"+e.getMessage());
		}
	}

	@Override
	public String help(String prefix) {
		return "Command to evaluate Code\n"
				+ "**CAN ONLY BE USED BY *the bot-admin***";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.ADMIN;
	}
}
