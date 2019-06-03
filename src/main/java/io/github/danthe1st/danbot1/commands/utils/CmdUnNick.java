package io.github.danthe1st.danbot1.commands.utils;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.util.ArrayList;
import java.util.List;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

/**
 * Command to unnick a {@link Member}
 * @author Daniel Schmid
 */
@BotCommand(aliases = "unnick")
public class CmdUnNick implements Command{
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return !((args.length==0&&!PermsCore.check(event, "unnick",false))||(!PermsCore.check(event, "unnick.others")));
	}
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if (args.length==0) {
			try {
				event.getGuild().modifyNickname(event.getGuild().getMember(event.getAuthor()), null).queue();
			} catch (InsufficientPermissionException e) {
				STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"errInsufficientDiscordPermissions"));
			}
			
			return;
		}
		//else
		List<Member> toNick=new ArrayList<>();
		for (String string : args) {
			try {
				toNick.add(event.getGuild().getMemberById(string));
			} catch (NumberFormatException e) {
				if(!toNick.addAll(event.getGuild().getMembersByEffectiveName(string, true))) {
					toNick.addAll(event.getGuild().getMembersByName(string, true));
				}
			}
		}
		for (Member member : toNick) {
			try {
				event.getGuild().modifyNickname(member, null).queue();
			} catch (InsufficientPermissionException e) {
				STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"errInsufficientDiscordPermissions"));
			}
		}
	}
	@Override
	public String help() {
		return "unnickHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.GUILD_MODERATION;
	}
}
