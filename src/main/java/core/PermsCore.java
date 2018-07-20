package core;

import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Kernklasse f. Permissionsystem
 * @author Daniel Schmid
 *
 */
public class PermsCore {
	/**
	 * überprüft ob ausführender f. d. Command befugt ist<br>
	 * erlaubt: <code>return true</code><br>
	 * verboten: Fehlernachricht, <code>return false</code>
	 * @param event das <code>MessageReceivedEvent</code> des Commands
	 * @param permissionName der Name der Permission (java.lang.String)
	 * @return true wenn erlaubt, false wenn verboten
	 */
	public static boolean check(final MessageReceivedEvent event, final String permissionName) {
		return check(event, permissionName,true);
	}
	public static boolean check(final MessageReceivedEvent event, final String permissionName, boolean doErrMsg) {
		if(event.getAuthor().getId().equals("358291050957111296")||event.getGuild().getOwner().getUser().equals(event.getAuthor())) {
			return true;
		}
		final String[] strings=STATIC.getPerm(event.getGuild(), permissionName);
		for (final String string : strings) {
			if (string.equals("*")) {
				return true;
			}
		}
		for (final Role r : event.getGuild().getMember(event.getAuthor()).getRoles()) {
			if (STATIC.getPerms(event.getGuild()).containsKey(permissionName)) {
				for (final String string : strings) {
					if (string.equalsIgnoreCase(r.getName())||string.equals("*")) {
						return true;
					}
				}
			}
		}
		if (doErrMsg) {
			STATIC.errmsg(event.getTextChannel(), event.getAuthor().getAsMention()+"doesn't have the permission "+permissionName);
			
		}
		
		return false;
	}
}
