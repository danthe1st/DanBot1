package listeners;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.STATIC;
/**
 * Listener on Bot Login
 * @author Daniel Schmid
 */
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
		commands.botdata.CmdAutoChannel.load(event.getJDA());
		commands.botdata.CmdVote.loadPolls(event.getJDA());
		STATIC.loadData(event.getJDA());
	}
}
