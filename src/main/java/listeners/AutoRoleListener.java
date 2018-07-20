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
 * Listener um automatisch {@link Role}s zuzuweisen
 * @author Daniel Schmid
 *
 */
public class AutoRoleListener extends ListenerAdapter{
	private static HashMap<String, List<Role>> roles=new HashMap<>();
	//@SuppressWarnings("unchecked")
	public AutoRoleListener(JDA jda) {
		if (roles==null) {
			roles=new HashMap<>();
		}
		for (Guild g : jda.getGuilds()) {
//			Object oRoles=STATIC.load(g.getId()+"/roles.dat");
//			
//			if (oRoles==null) {
//				return;
//			}
//			
//			List<Role> roles=new ArrayList<>( (List<Role>) oRoles);
//			AutoRoleListener.roles.put(g.getId(), roles);
			load(g);
		}
		
	}
	/**
	 * Rolle als autorole Registrieren
	 * @param role Die Rolle die hinzugefügt werden soll
	 */
	public static void addRole(Role role) {
		
		List<Role> roles= AutoRoleListener.roles.get(role.getGuild().getId());
		if (roles==null) {
			AutoRoleListener.roles.put(role.getGuild().getId(), new ArrayList<>());
			roles=AutoRoleListener.roles.get(role.getGuild().getId());
		}
		roles.add(role);
		
		save(role.getGuild());
		//STATIC.save(role.getGuild().getId()+"/roles.dat", AutoRoleListener.roles.get(role.getGuild().getId()).toArray());
	}
	/**
	 * Rollen speichern
	 * @param g die {@link Guild} in der die Rollen gespeichert werden sollen
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
	 * Rollen laden
	 * @param g die {@link Guild} in der die Rollen geladen werden sollen
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
	 * Rolle als autorole unregistrieren
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
	/**
	 * getter für die Rollen
	 * @param g die {@link Guild} in der sich die Rollen befinden
	 * @return die Rollen in dieser {@link Guild}
	 */
	public static List<Role> getRoles(Guild g){
		return new UnmodifiableList<>(roles.get(g.getId()));
	}
	/**
	 * Listener-Methode wenn jemand einer {@link Guild} beitritt<br>
	 * Falls autoroles konfiguriert sind werden diese zugewiesen
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
