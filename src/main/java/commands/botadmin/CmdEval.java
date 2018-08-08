package commands.botadmin;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import commands.Command;
/**
 * Command to Evaluate Code
 * @author Daniel Schmid
 */
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
	public void action(String[] args, MessageReceivedEvent event) {
		if (!event.getAuthor().getId().equals("358291050957111296")) {
			STATIC.errmsg(event.getTextChannel(), "This command can be *only* used by the developer of this Bot!");
			return;
		}
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
				+ "**CAN ONLY BE USED BY *Daniel Schmid***";
	}
	@Override
	public String getCommandType() {
		return CMD_TYPE_ADMIN;
	}
}
