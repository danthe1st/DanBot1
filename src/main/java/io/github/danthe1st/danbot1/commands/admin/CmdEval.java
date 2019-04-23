package io.github.danthe1st.danbot1.commands.admin;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.BotSecurityManager;
import io.github.danthe1st.danbot1.util.STATIC;
/**
 * Command to Evaluate Code
 * @author Daniel Schmid
 */
@BotCommand(aliases = "eval")
public class CmdEval implements Command{
	private ScriptEngine se;
	private static final String LATEST_EXCEPTION_KEY_NAME="latestException";
	
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
        Object result=null;
        try {
        	if (System.getSecurityManager() instanceof BotSecurityManager) {
        		result = ((BotSecurityManager)System.getSecurityManager()).execSecure((x)->{
					try {
						return se.eval(x);
					} catch (ScriptException e) {
						STATIC.errmsg(event.getTextChannel(), "Sorry, it didn't work\n"+e.getMessage());
						se.put(LATEST_EXCEPTION_KEY_NAME, e);
					}
					return null;
				}, script);
    		}
			
		} catch (Throwable e) {
			STATIC.errmsg(event.getTextChannel(), "An unknown Error occured ("+e.getClass().getName()+")\n"+e.getMessage());
			se.put(LATEST_EXCEPTION_KEY_NAME, e);
		}
        if (result != null) {
			STATIC.msg(event.getTextChannel(), result.toString());
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
