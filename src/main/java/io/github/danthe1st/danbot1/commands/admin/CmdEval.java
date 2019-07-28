package io.github.danthe1st.danbot1.commands.admin;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

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
@BotCommand("eval")
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
       
        StringBuilder scriptBuilder=new StringBuilder();
        for (String string : args) {
			scriptBuilder.append(string).append(" ");
		}
        Object result=null;
        try {
        	if (System.getSecurityManager() instanceof BotSecurityManager) {
        		result = ((BotSecurityManager)System.getSecurityManager()).execSecure(x->{
					try {
						return se.eval(x);
					} catch (ScriptException e) {
						STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(), "evalNotWork")+"\n"+e.getMessage());
						se.put(LATEST_EXCEPTION_KEY_NAME, e);
					}
					return null;
				}, scriptBuilder.toString());
    		}
			
		} catch (Exception e) {
			STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"evalUnknownError")+" ("+e.getClass().getName()+")\n"+e.getMessage());
			se.put(LATEST_EXCEPTION_KEY_NAME, e);
		}
        if (result != null) {
			STATIC.msg(event.getTextChannel(), result.toString());
		}
	}
	@Override
	public String help() {
		return "evalHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.ADMIN;
	}
}
