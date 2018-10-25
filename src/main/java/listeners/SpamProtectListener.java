package listeners;

import commands.moderation.nospam.SpamProtectionContainer;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class SpamProtectListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (SpamProtectionContainer.isGuildProtected(event.getGuild())) {
			SpamProtectionContainer.addMessage(event.getMessage());
		}
	}
	
	
	
}
