package io.github.danthe1st.danbot1.console;

import java.util.Map;

import net.dv8tion.jda.api.JDA;
/**
 * Console Command for help
 * @author Daniel Schmid
 */
public class CmdHelp implements Command{

	private Map<String, Command> commands;
	
	public CmdHelp(Map<String, Command> commands) {
		this.commands=commands;
	}
	
	@Override
	public void execute(JDA jda, String[] args) {
		StringBuilder sb=new StringBuilder();
		for (String command : commands.keySet()) {
			sb.append(command+":\n");
			sb.append(commands.get(command).help()+"\n\n");
		}
		System.out.println(sb.toString());
	}

	@Override
	public String help() {
		return "Prints help for all Console Commands\n"
				+ "Syntax: help";
	}

}