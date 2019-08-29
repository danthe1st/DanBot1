package io.github.danthe1st.danbot1.commands.utils;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

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
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Command to echo userinfo
 * @author Daniel Schmid
 */
@BotCommand("user")
public class CmdUser implements Command{
	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return PermsCore.check(event, "userinfo");
	}
	public void action(final String[] args, final GuildMessageReceivedEvent event) {
		if(args.length<1) {
			STATIC.errmsg(event.getChannel(), translate(event.getGuild(),"missingArgs"));
			return;
		}
		Set<Member> users=STATIC.getMembersFromMsg(event.getMessage());
		if (users.isEmpty()) {
			STATIC.errmsg(event.getChannel(), translate(event.getGuild(),"noUserFound"));
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
				emB.appendDescription(translate(event.getGuild(),"selfUserMessage")+event.getJDA().getUserById("358291050957111296").getAsMention()+" \n");
			}
			if (member.isOwner()) {
				emB.appendDescription(translate(event.getGuild(),"ownerMessage"));
			}
			if (member.getUser().isBot()) {
				emB.appendDescription(translate(event.getGuild(),"botMessage"));
			}
			emB.appendDescription(translate(event.getGuild(),"userFieldID")+"*"+member.getUser().getId()+"* \n");
			if (member.getNickname()!=null) {
				emB.appendDescription(translate(event.getGuild(),"userFieldNickname")+"*"+member.getNickname()+"* \n");
				emB.appendDescription(translate(event.getGuild(),"userFieldUsername")+"*"+member.getUser().getName()+"* \n");
			}
			if (!member.getActivities().isEmpty()) {
				emB.appendDescription(translate(event.getGuild(),"userFieldActivities"));
			}
			for (Activity activity : member.getActivities()) {
				emB.appendDescription("\t*"+activity.getName()+"* \n");
			}
			emB.appendDescription(translate(event.getGuild(),"userFieldJoined")+member.getTimeJoined().getDayOfMonth()+"."+member.getTimeJoined().getMonthValue()+"."+member.getTimeJoined().getYear()+"* \n");
			if (!member.getRoles().isEmpty()) {
				emB.appendDescription(translate(event.getGuild(),"userFieldRoles"));
				emB.appendDescription(translate(event.getGuild(),"userFieldMainRole")+"**"+member.getRoles().get(0).getName()+"** \n");
				if (member.getRoles().size()>1) {
					emB.appendDescription(translate(event.getGuild(),"userFieldOtherRoles"));
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
	public String help() {
		return "userHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}
}
