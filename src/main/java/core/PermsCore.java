package core;

import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Core Class for Permission System
 * @author Daniel Schmid
 */
public class PermsCore {
	/**
	 * tests if the who executed the Command is permitted to execute it
	 * if forbidden an errormessage will be sent.
	 * 
	 * @param event the {@link MessageReceivedEvent} of the Command-Message
	 * @param permissionName the name of the permission to test
	 * @return true if access else false
	 */
	public static boolean check(final MessageReceivedEvent event, final String permissionName) {
		return check(event, permissionName,true);
	}
	/**
	 * tests if the who executed the Command is permitted to execute it
	 * if forbidden and doErrMsg is true an errormessage will be sent.
	 * 
	 * @param event the {@link MessageReceivedEvent} of the Command-Message
	 * @param permissionName the name of the permission to test
	 * @param doErrMsg should an error Message be sent if the user is not permitted?
	 * @return true if access else false
	 */
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
	/**
	 * tests if the Author of a Message is the Developer of this Bot<br>
	 * if not an errormessage will be sent.
	 * @param event the {@link MessageReceivedEvent} of the Message
	 * @return <code>true</code> if the Author is the Developer, else <code>false</code>
	 */
	public static boolean checkOwner(MessageReceivedEvent event) {
		return checkOwner(event, true);
	}
	/**
	 * tests if the Author of a Message is the Developer of this Bot<br
	 * if forbidden and doErrMsg is true an errormessage will be sent.
	 * @param event the {@link MessageReceivedEvent} of the Message
	 * @param doErrMsg should an Error-Message be sent?
	 * @return <code>true</code> if the Author is the Developer, else <code>false</code>
	 */
	public static boolean checkOwner(MessageReceivedEvent event,boolean doErrMsg) {
		if(event.getAuthor().getId().equals("358291050957111296")||event.getGuild().getOwner().getUser().equals(event.getAuthor())) {
			return true;
		}
		if (doErrMsg) {
			STATIC.errmsg(event.getTextChannel(),"You need to be the developer of the Bot to do this!");
		}
		return false;
	}
}
