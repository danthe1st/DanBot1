package core;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import consoleCmd.CmdHelp;
import consoleCmd.CmdList;
import consoleCmd.CmdMsg;
import consoleCmd.CmdUser;
import consoleCmd.Command;
import net.dv8tion.jda.api.JDA;
/**
 * Core Class for the Console, for entering Commands with {@link System#in}
 * @author Daniel Schmid
 */
public class Console implements Runnable{
	private Scanner scan;
	private JDA jda;
	private Map<String,Command> commands=new HashMap<>();
	private ScriptEngine se = new ScriptEngineManager().getEngineByName("Nashorn");
	private static Console theConsole=null;
	public static synchronized void runConsole(Scanner scan,JDA jda) {
		if (theConsole!=null) {
			theConsole.jda=jda;
		}
		else {
			theConsole=new Console(scan, jda);
			Thread conThread=new Thread(theConsole, "Console");
			conThread.setDaemon(true);
			conThread.start();
		}
		
	}
	private Console(Scanner scan, JDA jda) {
		if (scan==null||jda==null) {
			throw new NullPointerException();
		}
		
		
		this.jda=jda;
		this.scan=scan;
		addCommands();
		se.put("jda", jda);
        se.put("System", System.class);
        try {
			se.eval("System=System.static");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
	private void addCommands() {
		commands.put("list", new CmdList());
		commands.put("msg", new CmdMsg());
		commands.put("user", new CmdUser());
		commands.put("help", new CmdHelp(commands));
	}

	@Override
	public void run() {
		while (true) {
			try {
				String msg;
				try {
					msg=scan.nextLine();
				} catch (NoSuchElementException|IllegalStateException e) {
					return;
				}
				if(!parse(msg)) {
					eval(msg);
				}
			} catch (Throwable problem) {
				problem.printStackTrace();
				System.err.println("unknown Error: "+problem.getMessage());
			}
		}
	}
	
	private boolean parse(String msg) {
		
		String[] splitted=msg.split(" ");
		String cmdName=splitted[0];
		String[] args=new String[splitted.length-1];
		if (splitted.length>1) {
			for (int i = 0; i < args.length; i++) {
				args[i]=splitted[i+1];
			}
		}
		
		for (String currentCmdName : commands.keySet()) {
			if (currentCmdName.equalsIgnoreCase(cmdName)) {
				Command cmd=commands.get(currentCmdName);
				cmd.execute(jda, args);
				return true;
			}
			continue;
		}
		return false;
	}
	private void eval(String code) {
        try {
        	
			Object ergebnis=se.eval(code);
			if (ergebnis != null) {
				System.out.println(ergebnis);
			}
			
		} catch (ScriptException e) {
			System.err.println("Sorry, it didn't work:"+e.getMessage());
			
		}
	}
}
