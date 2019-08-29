package io.github.danthe1st.danbot1.console;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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
	/**
	 * starts the Console 
	 * @param scan a Scanner to the Console input
	 * @param jda the JDA Object
	 */
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
	/**
	 * adds the standard console Commands
	 */
	private void addCommands() {
		addCommand("list", new CmdList());
		addCommand("msg", new CmdMsg());
		addCommand("user", new CmdUser());
		addCommand("help", new  CmdHelp(commands));
	}
	/**
	 * add a Console Command
	 * @param alias the name of the Command
	 * @param command the Command Object
	 */
	public void addCommand(String alias, Command command) {
		commands.put(alias, command);
	}
	/**
	 * runs the Console
	 */
	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				String msg;
				msg=scan.nextLine();
				if(!parse(msg)) {
					eval(msg);
				}
			}catch (NoSuchElementException|IllegalStateException e) {
				return;
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * parses a Command
	 * @param msg the input String
	 * @return <code>true</code>if the Command was found and executed successfully, else <code>false</code>
	 */
	private boolean parse(String msg) {
		String[] splitted=msg.split(" ");
		String cmdName=splitted[0];
		String[] args=new String[splitted.length-1];
		if (splitted.length>1) {
			for (int i = 0; i < args.length; i++) {
				args[i]=splitted[i+1];
			}
		}
		for (Entry<String, Command> entry : commands.entrySet()) {
			String currentCmdName=entry.getKey();
			if (currentCmdName.equalsIgnoreCase(cmdName)) {
				Command cmd=entry.getValue();
				try {
					cmd.execute(jda, args);
					return true;
				}catch (Exception e) {
					return false;
				}
			}
		}
		return false;
	}
	/**
	 * evaluates code using the Nashorn js Engine
	 * @param code
	 */
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
