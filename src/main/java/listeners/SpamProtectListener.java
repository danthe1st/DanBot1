package listeners;

import commands.moderation.nospam.SpamProtectionContainer;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class SpamProtectListener extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (SpamProtectionContainer.isGuildProtected(event.getGuild())) {
			SpamProtectionContainer.addMessage(event.getMessage());
		}
	}
}
