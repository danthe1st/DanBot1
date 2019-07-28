package io.github.danthe1st.danbot1.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import static io.github.danthe1st.danbot1.util.LanguageController.translate;
/**
 * Core Class for Permission System
 * @author Daniel Schmid
 */
public class PermsCore {
	//					gId				Perm	Role
	private static final Map<String, Map<String, String[]>> perms=new HashMap<>();
	private static final HashMap<String, String[]> STD_PERMS =new HashMap<>();
	
	private static final String DEFAULT_ROLE_ALL_PERMS="Owner";
	private static final String DEFAULT_ROLE_ADMINISTRATIVE_PERMS="Admin";
	private static final String DEFAULT_ROLE_CONTROL_PERMS_1="Moderator";
	private static final String DEFAULT_ROLE_CONTROL_PERMS_2="Supporter";
	private static final String DEFAULT_PERM_ALLOW_EVERYONE="*";
	
	static {
		STD_PERMS.put("ping", new String[] {DEFAULT_PERM_ALLOW_EVERYONE});
		STD_PERMS.put("motd", new String[] {DEFAULT_PERM_ALLOW_EVERYONE});
		STD_PERMS.put("motd.change", new String[] {DEFAULT_PERM_ALLOW_EVERYONE});
		STD_PERMS.put("say", new String[] {DEFAULT_PERM_ALLOW_EVERYONE});
		STD_PERMS.put("clearChat", new String[] {DEFAULT_ROLE_ALL_PERMS, DEFAULT_ROLE_ADMINISTRATIVE_PERMS, DEFAULT_ROLE_CONTROL_PERMS_1, DEFAULT_ROLE_CONTROL_PERMS_2});
		STD_PERMS.put("playMusic", new String[] {DEFAULT_PERM_ALLOW_EVERYONE});
		STD_PERMS.put("userphone", new String[] {DEFAULT_PERM_ALLOW_EVERYONE});
		STD_PERMS.put("vote", new String[] {DEFAULT_PERM_ALLOW_EVERYONE});
		STD_PERMS.put("vote.create", new String[] {DEFAULT_PERM_ALLOW_EVERYONE});
		STD_PERMS.put("vote.vote", new String[] {DEFAULT_PERM_ALLOW_EVERYONE});
		STD_PERMS.put("vote.close", STD_PERMS.get("vote.create"));
		STD_PERMS.put("vote.stats", STD_PERMS.get("vote.vote"));
		STD_PERMS.put("prefix", new String[] {DEFAULT_PERM_ALLOW_EVERYONE});
		STD_PERMS.put("prefix.set", new String[] {DEFAULT_ROLE_ALL_PERMS, DEFAULT_ROLE_ADMINISTRATIVE_PERMS});
		STD_PERMS.put("prefix.show", new String[] {DEFAULT_PERM_ALLOW_EVERYONE});
		STD_PERMS.put("autoChannel", new String[] {DEFAULT_ROLE_ALL_PERMS, DEFAULT_ROLE_ADMINISTRATIVE_PERMS});
		STD_PERMS.put("stop", new String[] {DEFAULT_ROLE_ALL_PERMS, DEFAULT_ROLE_ADMINISTRATIVE_PERMS});
		STD_PERMS.put("restart", new String[] {DEFAULT_ROLE_ALL_PERMS, DEFAULT_ROLE_ADMINISTRATIVE_PERMS});
		STD_PERMS.put("perm", new String[] {DEFAULT_PERM_ALLOW_EVERYONE});
		STD_PERMS.put("perm.get", STD_PERMS.get("perm"));
		STD_PERMS.put("perm.change", new String[] {DEFAULT_ROLE_ALL_PERMS});
		STD_PERMS.put("kick", new String[] {DEFAULT_ROLE_ALL_PERMS});
		STD_PERMS.put("ban", new String[] {DEFAULT_ROLE_ALL_PERMS});
		STD_PERMS.put("role", new String[] {DEFAULT_ROLE_ALL_PERMS});
		STD_PERMS.put("spam", new String[] {DEFAULT_ROLE_ALL_PERMS});
		STD_PERMS.put("logger", new String[] {DEFAULT_PERM_ALLOW_EVERYONE});
		STD_PERMS.put("logger.show", new String[] {DEFAULT_PERM_ALLOW_EVERYONE});
		STD_PERMS.put("logger.set", new String[] {DEFAULT_ROLE_ALL_PERMS, DEFAULT_ROLE_ADMINISTRATIVE_PERMS});
		STD_PERMS.put("userinfo", new String[] {DEFAULT_PERM_ALLOW_EVERYONE});
		STD_PERMS.put("autorole", new String[] {DEFAULT_ROLE_ALL_PERMS, DEFAULT_ROLE_ADMINISTRATIVE_PERMS});
		STD_PERMS.put("unnick.others", new String[] {DEFAULT_ROLE_ALL_PERMS, DEFAULT_ROLE_ADMINISTRATIVE_PERMS, DEFAULT_ROLE_CONTROL_PERMS_1, DEFAULT_ROLE_CONTROL_PERMS_2});
		STD_PERMS.put("unnick", new String[] {DEFAULT_PERM_ALLOW_EVERYONE});
		STD_PERMS.put("dice", new String[] {DEFAULT_PERM_ALLOW_EVERYONE});
		STD_PERMS.put("vkick", new String[] {DEFAULT_ROLE_ALL_PERMS, DEFAULT_ROLE_ADMINISTRATIVE_PERMS, DEFAULT_ROLE_CONTROL_PERMS_1, DEFAULT_ROLE_CONTROL_PERMS_2});
		STD_PERMS.put("changeLanguage", new String[] {DEFAULT_ROLE_ALL_PERMS, DEFAULT_ROLE_ADMINISTRATIVE_PERMS});
	}
	private PermsCore() {
		//private constructor to prevent instantiation
	}
	/**
	 * tests if the who executed the Command is permitted to execute it
	 * if forbidden an errormessage will be sent.
	 * 
	 * @param event the {@link MessageReceivedEvent} of the Command-Message
	 * @param permissionName the name of the permission to test
	 * @return true if access else false
	 */
	public static boolean check(final MessageReceivedEvent event, final String permissionName) {
		return check(event, permissionName,true);
	}
	/**
	 * tests if the who executed the Command is permitted to execute it
	 * if forbidden and doErrMsg is true an errormessage will be sent.
	 * 
	 * @param event the {@link MessageReceivedEvent} of the Command-Message
	 * @param permissionName the name of the permission to test
	 * @param doErrMsg should an error Message be sent if the user is not permitted?
	 * @return true if access else false
	 */
	public static boolean check(final MessageReceivedEvent event, final String permissionName, boolean doErrMsg) {
		if(event.getAuthor().getId().equals(Main.getAdminId())||event.getGuild().getOwner().getUser().equals(event.getAuthor())) {
			return true;
		}
		final String[] strings=getPerm(event.getGuild(), permissionName);
		for (final String string : strings) {
			if (string.equals(DEFAULT_PERM_ALLOW_EVERYONE)) {
				return true;
			}
		}
		for (final Role r : event.getGuild().getMember(event.getAuthor()).getRoles()) {
			if (getPerms(event.getGuild()).containsKey(permissionName)) {
				for (final String string : strings) {
					if (string.equalsIgnoreCase(r.getId())||string.equals(DEFAULT_PERM_ALLOW_EVERYONE)) {
						return true;
					}
				}
			}
		}
		if (doErrMsg) {
			STATIC.errmsg(event.getTextChannel(), event.getAuthor().getAsMention()+translate(event.getGuild(),"errMissingPermission")+permissionName);
			
		}
		return false;
	}
	/**
	 * tests if the Author of a Message is the Developer of this Bot<br>
	 * if not an errormessage will be sent.
	 * @param event the {@link MessageReceivedEvent} of the Message
	 * @return <code>true</code> if the Author is the Developer, else <code>false</code>
	 */
	public static boolean checkOwner(MessageReceivedEvent event) {
		return checkOwner(event, true);
	}
	/**
	 * tests if the Author of a Message is the Developer of this Bot<br>
	 * if forbidden and doErrMsg is true an errormessage will be sent.
	 * @param event the {@link MessageReceivedEvent} of the Message
	 * @param doErrMsg should an Error-Message be sent?
	 * @return <code>true</code> if the Author is the Developer, else <code>false</code>
	 */
	public static boolean checkOwner(MessageReceivedEvent event,boolean doErrMsg) {
		if(event.getAuthor().getId().equals(Main.getAdminId())) {
			return true;
		}
		if (doErrMsg) {
			STATIC.errmsg(event.getTextChannel(),translate(event.getGuild(),"errNoBotAdmin"));
		}
		return false;
	}
	/**
	 * gets guild-local Permission info using a specified permission name
	 * @param g The {@link Guild} from the Permission
	 * @param permName The name of the Permission
	 * @return A String[] of the Role names which have the Permission
	 */
	public static String[] getPerm(Guild g, String permName) {
		Map<String, String[]> guildPerms = perms.get(g.getId());
		if (perms.get(g.getId())!=null) {
			if (guildPerms.get(permName)==null) {//permission not found
				return new String[0];
			}
			return guildPerms.get(permName);
		}
		return getRoleIDsFromNames(STD_PERMS.get(permName), g);
	}
	
	/**
	 * gets the Permissions of a {@link Guild}
	 * @param g The Guild(Discord-Server)
	 * @return A {@link Map} of the Permissions
	 */
	public static Map<String, String[]> getPerms(Guild g) {
		
		try {
			if(perms.get(g.getId())!=null) {
				return perms.get(g.getId());
			}
		} catch (Exception e) {
			//ignore
		}
		return getStdPermsAsIds(g);
	}
	public static void resetPerms(Guild g) {
		Map<String, String[]> guildPerms=new HashMap<>();
		
		
		STD_PERMS.forEach((permName,roles)->
			guildPerms.put(permName, getRoleIDsFromNames(roles, g))
		);
		
		perms.put(g.getId(), guildPerms);
		savePerms(g);
	}
	
	/**
	 * deletes a <b>userdefined</b> Permission in a {@link Guild}
	 * @param g The Guild(Discord-Server)
	 * @param permName The name of the Permission which should be deleted
	 * @return true if something changed
	 */
	public static boolean removePerm(Guild g, String permName) {
		Map<String, String[]> guildPerms = perms.get(g.getId());
		
		if (guildPerms==null) {
			resetPerms(g);
			guildPerms=perms.get(g.getId());
		}
		if (guildPerms.containsKey(permName)) {
			if (STD_PERMS.containsKey(permName)) {
				return false;
			}
			perms.get(g.getId()).remove(permName);
			savePerms(g);
			return true;
		}
		return false;
	}
	
	/**
	 * replaces all occurencies of a Role to an another Role in a specified {@link Guild}
	 * @param g The Guild(Discord-Server)
	 * @param roleToChange The name of the Role should be replaced
	 * @param newRole The name of the Role to Replace with
	 */
	public static void chRole(Guild g, String roleToChange, String newRole) {
		roleToChange=getRoleIDFromName(roleToChange, g);
		newRole=getRoleIDFromName(newRole, g);
		if (roleToChange==null) {
			return;
		}
		Map<String, String[]> guildPerms = perms.get(g.getId());
		if (guildPerms==null) {
			resetPerms(g);
		}
		for (String[] perm : perms.get(g.getId()).values()) {
			for (int i = 0; i < perm.length; i++) {
				if (perm[i].equals(roleToChange)) {
					perm[i]=newRole;
				}
			}
		}
		savePerms(g);
	}
	
	/**
	 * reloads new Permissions(came with a Bot Update)
	 * @param g the {@link Guild} to Reload
	 */
	public static void reloadPerms(Guild g) {
		loadPerms(g);
		Map<String, String[]> perms=PermsCore.perms.get(g.getId());
		if (perms==null) {
			PermsCore.perms.put(g.getId(), getStdPermsAsIds(g));
			return;
		}
		STD_PERMS.forEach((permName,perm)->{
			if (!(perms.containsKey(permName))) {
				perms.put(permName,getRoleIDsFromNames(perm,g));
			}
		});
		savePerms(g);
	}
	
	/**
	 * sets a Permission in a {@link Guild}
	 * @param g The Guild(Discord-Server)
	 * @param permName The Name of the Permission
	 * @param perm The names of the Roles who should have the Permission
	 */
	public static void setPerm(Guild g, String permName, String[] perm) {
		
		try {
			if(g!=null) {
				
				if (!perms.containsKey(g.getId())) {
					perms.put(g.getId(), getStdPermsAsIds(g));
					
				}
				perms.get(g.getId()).put(permName, perm);
				
				int num=0;
				for (String string : perms.get(g.getId()).get(permName)) {
					if(string!=null) {
						num++;
					}
				}
				String[] permsNew=new String[num];
				int j=0;
				for (int i = 0; i < perm.length; i++) {
					if (perm[i]!=null) {
						permsNew[j]=perm[i];
						j++;
					}
				}
				perms.get(g.getId()).put(permName, permsNew);
				savePerms(g);
			}
		} catch (Exception e) {e.printStackTrace();}
	}
	private static final String SAVE_NAME="perms.dat";
	/**
	 * saves the Permissions of a {@link Guild}
	 * @param g The Guild(Discord-Server)
	 */
	public static void savePerms(Guild g) {
		Map<String, String[]> guildPerms = perms.get(g.getId());
		if (guildPerms==null) {
			return;
		}
		File dir=new File(STATIC.getSettingsDir()+"/"+g.getId());
		if (!dir.exists()) {
			try {
				Files.createDirectory(dir.toPath());
			} catch (IOException e) {
				System.err.println("cannot create directory: "+dir.getAbsolutePath());
			}
		}
		File file=new File(dir,SAVE_NAME);
		if (!file.exists()) {
			try {
				Files.createFile(file.toPath());
			} catch (IOException e) {
				//ignore
			}
		}
		STATIC.save(g.getId()+"/"+SAVE_NAME, guildPerms);
	}
	/**
	 * Loads the Permissions of a {@link Guild}
	 * @param g The Guild(Discord-Server)
	 */
	@SuppressWarnings("unchecked")
	public static void loadPerms(Guild g) {
		File dir=new File(STATIC.getSettingsDir()+"/"+g.getId());
		if (!dir.exists()) {
			return;
		}
		final File file=new File(dir,SAVE_NAME);
		if (!file.exists()) {
			return;
		}
		perms.put(g.getId(), (Map<String, String[]>) STATIC.load(g.getId()+"/"+SAVE_NAME));
	}
	/**
	 * loads the Permissions of all {@link Guild}s
	 * @param jda the {@link JDA} instance
	 */
	public static void loadPerms(JDA jda) {
		for (Guild g : jda.getGuilds()) {
			loadPerms(g);
		}
	}
	/**
	 * get the ISnowflake Id from a role name or null if the Role doesn't exist
	 * @param name the name of the Role
	 * @param g the Guild the Role is in
	 * @return the id of the role or <code>null</code>
	 */
	public static String getRoleIDFromName(String name,Guild g) {
		if (name==null) {
			return null;
		}
		if (name.equals("")) {
			return null;
		}
		if (name.equals(DEFAULT_PERM_ALLOW_EVERYONE)) {
			return name;
		}
		List<Role> roles=g.getRolesByName(name, true);
		if (roles.isEmpty()) {
			return null;
		}
		Role role=roles.get(0);
		if (role==null) {
			return null;
		}
		return role.getId();
	}
	/**
	 * get the ISnowflake Ids from an Array of role names 
	 * @param names the Array of role names
	 * @param g the Guild the Roles are in
	 * @return the ids of the roles represented by a {@link String} array
	 */
	public static String[] getRoleIDsFromNames(String[] names,Guild g) {
		if (names==null) {
			return new String[0];
		}
		
		List<String> roleIds=new ArrayList<>();
		for (String role : names) {
			String roleId;
			if (role==null||role.equals("")) {
				roleId = null;
			}else {
				roleId=getRoleIDFromName(role, g);
			}
			if (roleId!=null) {
				roleIds.add(roleId);
			}
			
		}
		
		return roleIds.toArray(new String[roleIds.size()]);
	}
	/**
	 * gets the standard Permissions for a Guild specified as ISnowflake ids
	 * @param g the {@link Guild}
	 * @return the standard-permissions for the guild
	 */
	public static Map<String, String[]> getStdPermsAsIds(Guild g){
		Map<String, String[]> perms=new HashMap<>();
		STD_PERMS.forEach((permName,permData)->
			perms.put(permName, getRoleIDsFromNames(permData, g))
		);
		return perms;
	}
}
