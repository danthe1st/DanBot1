package io.github.danthe1st.danbot1.commands.utils;

import java.awt.Color;
import java.util.Set;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
/**
 * Command to echo userinfo
 * @author Daniel Schmid
 */
@BotCommand(aliases = "user")
public class CmdUser implements Command{
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.check(event, "userinfo");
	}
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(args.length<1) {
			STATIC.errmsg(event.getTextChannel(), "Not anough arguments.");
			return;
		}
		Set<Member> users=STATIC.getMembersFromMsg(event.getMessage());
		if (users.isEmpty()) {
			STATIC.errmsg(event.getTextChannel(), "User not found");
				
				return;
		}
		EmbedBuilder emB=new EmbedBuilder().setColor(Color.gray);
		boolean first=true;
		for (Member member : users) {
			if (!first) {
				emB.appendDescription("----------------------------- \n\n\n");
			}
			emB.appendDescription("***"+member.getEffectiveName()+"*** \n\n");
			if (member.getUser().equals(event.getJDA().getSelfUser())) {
				emB.appendDescription("is a ***cool*** Bot by User "+event.getJDA().getUserById("358291050957111296").getAsMention()+" \n");
			}
			if (member.isOwner()) {
				emB.appendDescription("is the **Owner** of this Server \n");
			}
			if (member.getUser().isBot()) {
				emB.appendDescription("is a **Bot**\n");
			}
			if (member.getUser().isFake()) {
				emB.appendDescription("is **Fake**\n");
			}
			emB.appendDescription("**id**: *"+member.getUser().getId()+"* \n");
			if (member.getNickname()!=null) {
				emB.appendDescription("**Nickname**: *"+member.getNickname()+"* \n");
				emB.appendDescription("**Real Name**: *"+member.getUser().getName()+"* \n");
			}
			if (!member.getActivities().isEmpty()) {
				emB.appendDescription("**Games**\n");
			}
			for (Activity activity : member.getActivities()) {
				emB.appendDescription("\t*"+activity.getName()+"* \n");
			}
			emB.appendDescription("**joined** the Guild: *"+member.getTimeJoined().getDayOfMonth()+"."+member.getTimeJoined().getMonthValue()+"."+member.getTimeJoined().getYear()+"* \n");
			if (!member.getRoles().isEmpty()) {
				emB.appendDescription("***Roles***: \n");
				emB.appendDescription("*Main Role*: **"+member.getRoles().get(0).getName()+"** \n");
				if (member.getRoles().size()>1) {
					emB.appendDescription("other Roles: ");
					for (int i = 1; i < member.getRoles().size(); i++) {
						emB.appendDescription("**"+member.getRoles().get(i).getName()+"** ");
					}
					emB.appendDescription("\n");
				}
			}
			first=false;
		}
		event.getAuthor().openPrivateChannel().complete().sendMessage(emB.build()).queue();
	}
	public String help(String prefix) {
		return "Writes information about a member of the guild\n"
				+ "(see Permission *userinfo* in Command perm get)\n"
				+"*Syntax*: "+prefix+"user <username, nickname or id>";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}
}
