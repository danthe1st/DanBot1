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
		commands.forEach((alias,cmd)->{
			sb.append(alias).append(":\n");
			sb.append(cmd.help()).append("\n\n");
		});
		System.out.println(sb.toString());
	}

	@Override
	public String help() {
		return "Prints help for all Console Commands\n"
				+ "Syntax: help";
	}

}
