package io.github.danthe1st.danbot1.commands.moderation;

import java.util.List;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
/**
 * Command to move {@link Member}s from their current {@link VoiceChannel} to the AFK-{@link VoiceChannel}
 * @author Daniel Schmid
 */
@BotCommand(aliases = {"vkick"})
public class CmdVoiceKick implements Command{

	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.check(event, "vkick");
	}
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		List<Member> toKick=STATIC.getMembersFromMsg(event.getMessage());
		try {
			for (Member member : toKick) {
				if (member.getVoiceState().inVoiceChannel()) {
					event.getGuild().getController().moveVoiceMember(member, event.getGuild().getAfkChannel()).queue();;
				}
				else {
					STATIC.errmsg(event.getTextChannel(), member.getEffectiveName()+" is not in a Voice Channel.");
				}
			}
		} catch (InsufficientPermissionException e) {
			STATIC.errmsg(event.getTextChannel(), "DanBot1 is missing Permissions.");
		} catch (NullPointerException e) {
			STATIC.errmsg(event.getTextChannel(), "No AFK Channel found.");
		}
	}
	@Override
	public String help(String prefix) {
		return "kicks a user out of a voice Channel\n"
				+ "(see Permission *vkick* in Command perm get)\n"
				+"*Syntax*: "+prefix+"vkick <user>";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.GUILD_MODERATION;
	}

}
