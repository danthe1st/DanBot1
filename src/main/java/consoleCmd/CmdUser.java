package consoleCmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

public class CmdUser implements Command{

	@Override
	public void execute(JDA jda, String[] args) {
		if(args.length<1) {
			System.err.println("Not anough arguments.");
			return;
		}
		List<User> users=new ArrayList<>();
		for (String uname : args) {
			
			for (Guild guild : jda.getGuilds()) {
				for (Member member : guild.getMembersByEffectiveName(uname, true)) {
					if (!users.contains(member.getUser())) {
						users.add(member.getUser());
					}
					
				}
				for (Member member : guild.getMembersByName(uname, true)) {
					if (!users.contains(member.getUser())) {
						users.add(member.getUser());
					}
				}
				for (Member member : guild.getMembersByNickname(uname, true)) {
					if (!users.contains(member.getUser())) {
						users.add(member.getUser());
					}
				}
				try {
					if (!users.contains(jda.getUserById(uname))) {
						users.add(jda.getUserById(uname));
					}
					
				} catch (NumberFormatException e) {
				}
				
			}
		}
		
		if (users.isEmpty()) {
				System.err.println(("User not found"));
				return;
		}
		
		StringBuilder sb=new StringBuilder();
		boolean first=true;
		for (User user : users) {
			if (!first) {
				sb.append("----------------------------- \n\n\n");
			}
			sb.append(""+user.getName()+"\n\n");
			List<Member> allMembers=new ArrayList<>();
			
			List<Guild> owner=new ArrayList<>();
			Game game=null;
			Map<Guild, List<Role>> roles=new HashMap<>();
			for (Guild guild : jda.getGuilds()) {
				Member member=guild.getMember(user);
				if (member != null) {
					allMembers.add(member);
					if (member.isOwner()) {
						owner.add(guild);
					}
					if (game==null) {
						game=member.getGame();
					}
					List<Role> rolesOnGuild=new ArrayList<>();
					for (Role role : member.getRoles()) {
						rolesOnGuild.add(role);
					}
					roles.put(guild, rolesOnGuild);
				}
				
			}
			if (!owner.isEmpty()) {
				sb.append("is the Owner of this Servers: ");
				first=true;
				for (Guild guild : owner) {
					if (!first) {
						sb.append(" | ");
					}
					sb.append(guild.getName());
					
				}
				sb.append("\n");
			}
			if (user.isBot()) {
				sb.append("is a Bot\n");
			}
			if (user.isFake()) {
				sb.append("is Fake\n");
			}
			sb.append("id: "+user.getId()+" \n");
			if (game!=null) {
				sb.append("Game: "+game.getName()+" \n");
			}
			sb.append("Roles:\n");
			for (Guild g : jda.getGuilds()) {
				if (g.getMember(user)!=null) {
					Member member=g.getMember(user);
					sb.append(g.getName()+": \n");
					sb.append("\tMain Role: "+member.getRoles().get(0).getName()+" \n");
					if (member.getRoles().size()>1) {
						sb.append("\tother Roles: ");
						for (int i = 1; i < member.getRoles().size(); i++) {
							sb.append(""+member.getRoles().get(i).getName()+" ");
						}
						sb.append("\n");
					}
				}
			}
			
			
			first=false;
			
		}
		System.out.println(sb.toString());
	}

	@Override
	public String help() {
		return "gets information about users\n"
				+ "Syntax: user <Username/Nickname/ID of first user> <Username/Nickname/ID of second user> ...";
	}

}
