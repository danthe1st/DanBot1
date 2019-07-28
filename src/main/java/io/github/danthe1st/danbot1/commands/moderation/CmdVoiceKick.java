package io.github.danthe1st.danbot1.commands.moderation;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.util.Set;

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
@BotCommand("vkick")
public class CmdVoiceKick implements Command{

	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.check(event, "vkick");
	}
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		Set<Member> toKick=STATIC.getMembersFromMsg(event.getMessage());
		try {
			for (Member member : toKick) {
				if (member.getVoiceState().inVoiceChannel()) {
					event.getGuild().moveVoiceMember(member, event.getGuild().getAfkChannel()).queue();
				}
				else {
					STATIC.errmsg(event.getTextChannel(), member.getEffectiveName()+translate(event.getGuild(),"errUserNotInVC"));
				}
			}
		} catch (InsufficientPermissionException e) {
			STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"errInsufficientDiscordPermissions"));
		} catch (NullPointerException e) {
			STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"noAFKChanFound"));
		}
	}
	@Override
	public String help() {
		return "vkickHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.GUILD_MODERATION;
	}

}
