package consoleCmd;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class CmdList implements Command{

	@Override
	public void execute(JDA jda, String[] args) {
		boolean vchan=false;
		
		boolean tchan=false;
		List<Guild> guilds=null;
		for (String arg : args) {
			String tmpguildId=arg;
			try {
				
			
				if(jda.getGuildById(tmpguildId)!=null) {
					if (guilds==null) {
						guilds=new ArrayList<>();
					}
					guilds.add(jda.getGuildById(tmpguildId));
					continue;
				} else{
					
				}
			} catch (Exception e) {
				
			}
			if (!vchan) {
				vchan=(arg.equalsIgnoreCase("*")||arg.startsWith("v"));
				
			}
			if (!tchan) {
				tchan=(arg.equalsIgnoreCase("*")||arg.startsWith("t"));
			}
			
		}
		
		
		
		
//		if (guildId!=null&&!guildId.equals("")) {
//			guilds=new ArrayList<>();
//			guilds.add(jda.getGuildById(guildId));
//		}
		StringBuilder sb=new StringBuilder();
		if (guilds==null||guilds.isEmpty()) {
			guilds=jda.getGuilds();
		}
		
		for (Guild guild : guilds) {
			sb.append(guild.getName()+"["+guild.getId()+"]:\n");
			if (tchan) {
				sb.append("Textchannels:\n");
				for (TextChannel channel : guild.getTextChannels()) {
					sb.append("\t"+channel.getName()+"["+channel.getId()+"]:"+channel.canTalk()+"\n");
				}
			}
			
			if (vchan) {
				sb.append("Voicechannels:\n");
				for (VoiceChannel channel : guild.getVoiceChannels()) {
					sb.append("\t"+channel.getName()+"["+channel.getId()+"]\n");
				}
			}
				
			
			sb.append("------------------------------\n");
		}
		
		System.out.println(sb.toString());
	}

	@Override
	public String help() {
		return "Lists channels or Guilds the Bot is active\n"
				+ "Can list all channels in the guilds, the TextChannnels or the VoiceChannels\n"
				+ "Syntax: list (<*|v|t>) (<IDs of the Guilds where you want to list channels>)";
	}

	
	
}
