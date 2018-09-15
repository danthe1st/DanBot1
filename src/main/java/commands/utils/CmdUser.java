package commands.utils;

import java.awt.Color;
import java.util.List;

import commands.Command;
import commands.CommandType;
import core.PermsCore;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command to echo userinfo
 * @author Daniel Schmid
 */
public class CmdUser implements Command{
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(!PermsCore.check(event, "userinfo")) {
			return;
		}		
		
		if(args.length<1) {
			STATIC.errmsg(event.getTextChannel(), "Not anough arguments.");
			return;
		}
		List<Member> users=STATIC.getMembersFromMsg(event.getMessage());
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
			if (member.getGame()!=null) {
				emB.appendDescription("**Game**: *"+member.getGame().getName()+"* \n");
			}
			emB.appendDescription("**joined** the Guild: *"+member.getJoinDate().getDayOfMonth()+"."+member.getJoinDate().getMonthValue()+"."+member.getJoinDate().getYear()+"* \n");
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
