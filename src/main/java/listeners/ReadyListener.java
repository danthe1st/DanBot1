package listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.STATIC;
/**
 * Listener on Bot Login
 * @author Daniel Schmid
 */
@BotListener
public class ReadyListener extends ListenerAdapter {
	/**
	 * on login:<br>
	 * prints all {@link Guild}s the Bot is in with {@link System#out}
	 */
	public void onReady(final ReadyEvent event) {
		System.out.println("\nThis Bot is running on following servers: \n");
		for (final Guild g : event.getJDA().getGuilds()) {
			System.out.println(STATIC.getServerData(g));
//			try {
//				g.getController().setNickname(g.getMember(event.getJDA().getSelfUser()), Main.getNickname()).queue();
//			} catch (Exception e) {}
		}
		//preload Data
		System.out.println("Loading data...");
		
		
		STATIC.loadData(event.getJDA());
		System.out.println("loaded");
	}
}
