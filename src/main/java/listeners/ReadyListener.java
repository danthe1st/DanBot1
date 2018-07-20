package listeners;

import core.Main;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.STATIC;
/**
 * Listener f. start
 * @author Daniel Schmid
 */
public class ReadyListener extends ListenerAdapter {
	/**
	 * wenn gestartet:<br>
	 * Gibt die Server in denen der Bot aktiv ist mitsamt ID und einminütiger Einladung mit sysout aus.
	 */
	public void onReady(final ReadyEvent event) {
		System.out.println("\nThis Bot is running on following servers: \n");
		for (final Guild g : event.getJDA().getGuilds()) {
			System.out.println(STATIC.getServerData(g));
			try {
				g.getController().setNickname(g.getMember(event.getJDA().getSelfUser()), Main.getNickname()).queue();
			} catch (Exception e) {}
		}
		//preload Data
		commands.utils.CmdAutoChannel.load(event.getJDA());
		commands.botdata.CmdVote.loadPolls(event.getJDA());
		STATIC.loadData(event.getJDA());
	}
}
