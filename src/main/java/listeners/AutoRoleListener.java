package listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.list.UnmodifiableList;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.STATIC;
/**
 * Listener to automatically assign roles
 * @author Daniel Schmid
 */
public class AutoRoleListener extends ListenerAdapter{
	private static HashMap<String, List<Role>> roles=new HashMap<>();
	public AutoRoleListener(JDA jda) {
		if (roles==null) {
			roles=new HashMap<>();
		}
		for (Guild g : jda.getGuilds()) {
			load(g);
		}
		
	}
	/**
	 * register a role as an autorole
	 * @param role the role to be registered
	 */
	public static void addRole(Role role) {
		
		List<Role> roles= AutoRoleListener.roles.get(role.getGuild().getId());
		if (roles==null) {
			AutoRoleListener.roles.put(role.getGuild().getId(), new ArrayList<>());
			roles=AutoRoleListener.roles.get(role.getGuild().getId());
		}
		roles.add(role);
		
		save(role.getGuild());
	}
	/**
	 * save autoroles for a {@link Guild}
	 * @param g the {@link Guild} where autoroles should be saved
	 */
	private static void save(Guild g) {
		List<Role> roles=AutoRoleListener.roles.get(g.getId());
		String[] roleIDs=new String[roles.size()];
		int num=0;
		for (Role role : roles) {
			roleIDs[num]=role.getId();
			num++;
		}
		STATIC.save(g.getId()+"/roles.dat", roleIDs);
	}
	/**
	 * loads autoroles for a {@link Guild}
	 * @param g the {@link Guild} where autoroles should be loaded
	 */
	private static void load(Guild g) {
		String[] roleIDs=(String[]) STATIC.load(g.getId()+"/roles.dat");
		if (roleIDs==null) {
			return;
		}
		List<Role> roles=new ArrayList<>();
		for (String string : roleIDs) {
			roles.add(g.getRoleById(string));
		}
		AutoRoleListener.roles.put(g.getId(), roles);
	}
	/**
	 * unregister an autorole
	 * @param role Die Rolle die gelöscht werden soll
	 */
	public static void removeRole(Role role) {
		List<Role> roles= AutoRoleListener.roles.get(role.getGuild().getId());
		if (roles==null) {
			return;
		}
		roles.remove(role);
		STATIC.save(role.getGuild().getId()+"/roles.dat", AutoRoleListener.roles.get(role.getGuild().getId()).toArray());
	}
	public static List<Role> getRoles(Guild g){
		return new UnmodifiableList<>(roles.get(g.getId()));
	}
	/**
	 * Listener when someone joines a {@link Guild} and there are autoroles the roles will be added to the user
	 */
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		if (roles==null||roles.isEmpty()) {
			return;
		}
		Guild g=event.getGuild();
		Member member=event.getMember();
		try {
			g.getController().addRolesToMember(member, roles.get(g.getId())).queue();
		} catch (Exception e) {
			
		}
	}
}
