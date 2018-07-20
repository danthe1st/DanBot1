package commands.utils;

import java.awt.Color;
import java.util.List;

import commands.Command;
import core.PermsCore;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command f. Userinfo (gibt vorhandene Informationen zu einem Benutzer aus)
 * @author Daniel Schmid
 *
 */
public class CmdUser implements Command{
	/**
	 * Der Befehl selbst(siehe help)
	 */
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(!PermsCore.check(event, "userinfo")) {
			return;
		}		
		
		if(args.length<1) {
			STATIC.errmsg(event.getTextChannel(), "Not anough arguments.");
			return;
		}
		List<Member> users=STATIC.getMembersFromMsg(event.getMessage());
//		for (String uname : args) {
//			
//			ArrayList<Member> usersTmp=new ArrayList<>();
//		 	usersTmp.addAll(event.getGuild().getMembersByEffectiveName(uname, true));
//			if (usersTmp.isEmpty()) {
//				List<Member> members = event.getGuild().getMembersByName(uname, true);
//				for (Member member : members) {
//					usersTmp.add(member);
//				}
//			}
//			if (usersTmp.isEmpty()) {
//				List<Member> members=event.getGuild().getMembersByNickname(uname, true);
//				for (Member member : members) {
//					usersTmp.add(member);
//				}
//			}
//			if (usersTmp.isEmpty()) {
//				
//				try {
//					usersTmp.add((event.getGuild().getMemberById(uname)));
//				} catch (Exception e) {
//				}
//				
//				
//			}
//			for (Member member : usersTmp) {
//				users.add(member);
//			}
//		}
		
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

	/**
	 * hilfe: gibt Hilfe zu diesem Command als String zur�ck
	 */
	public String help(String prefix) {
		return "Writes information about a member of the guild\n"
				+ "(see Permission *userinfo* in Command perm get)\n"
				+"*Syntax*: "+prefix+"user <username, nickname or id>";

	}

	@Override
	public String getCommandType() {
		return CMD_TYPE_USER;
	}
}
