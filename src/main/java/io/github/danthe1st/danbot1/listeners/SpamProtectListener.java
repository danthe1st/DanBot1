package io.github.danthe1st.danbot1.listeners;

import io.github.danthe1st.danbot1.commands.moderation.nospam.SpamProtectionContainer;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
/**
 * Listener for the spam-protection<br>
 * @author Daniel Schmid
 */
@BotListener
public class SpamProtectListener extends ListenerAdapter {
	/**
	 * listener to call the spam protection when a Message in a protected Guild is sent.
	 */
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (SpamProtectionContainer.isGuildProtected(event.getGuild())) {
			SpamProtectionContainer.addMessage(event.getMessage());
		}
	}
}
