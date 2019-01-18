package consoleCmd;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
/**
 * Console Command to write a Message
 * @author Daniel Schmid
 *
 */
public class CmdMsg implements Command{

	@Override
	public void execute(JDA jda, String[] args) {
		if (args.length<2) {
			System.err.println("missing args!");
		}
		TextChannel tc=null;
		String txt="";
		Color color=Color.GRAY;
		boolean exec=false;
		for (int i = 1; i < args.length; i++) {
			if (!exec&&args.length>i+1) {
				try {
					int rgb=Integer.parseInt(args[i]);
					color=new Color(rgb);
					continue;
				} catch (Exception e) {
					
				}
			}
			
			txt=""+args[i];
		}
		try {
			tc=jda.getTextChannelById(args[0]);
			
			tc.sendMessage(new EmbedBuilder()
					.setColor(color)
					.setDescription(txt)
					.build()).queue();
		} catch (Exception e) {
			System.err.println("Error");
		}
	}

	@Override
	public String help() {
		return "Sends a message in a Discord Text Channel\n"
				+ "Syntax: msg <ID of the Channel> <Message>";
	}

}
