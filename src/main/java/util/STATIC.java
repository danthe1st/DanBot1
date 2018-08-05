package util;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import commands.multicolor.MultiColorChanger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Invite;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

/**
 * Class for Constants, data and utility-methods
 * @author Daniel Schmid
 */
public final class STATIC {
	private STATIC(){}
	private static HashMap<Guild, String> prefixe=new HashMap<>();
	private static final String PREFIX="--";
	
	public static final String VERSION="v2.living";
	private static final HashMap<String, String[]> PERMS =new HashMap<String, String[]>();
	private static final String SETTINGS_DIR="./SERVER_SETTINGS";
	public static final String AUTOCHANNEL_POSTFIX=" [Autochannel]";
	public static final int INFO_TIMEOUT=5000;
	private static final String STD_CMD_LOGGER_NAME="cmdLog";
	private static HashMap<String, String> cmdLoggerNames =new HashMap<String, String>();
	private static final HashMap<Guild, HashMap<String, String[]>> permsLocal=new HashMap<Guild, HashMap<String, String[]>>();
	private static final Map<String, String> markupEscapeMap=new HashMap<>();
	/**
	 * initialize permissions and markup escapes
	 */
	static {
		PERMS.put("ping", new String[] {"*"});
		PERMS.put("motd", new String[] {"*"});
		PERMS.put("motd.change", new String[] {"*"});
		PERMS.put("say", new String[] {"*"});
		PERMS.put("clearChat", new String[] {"Owner", "Admin", "Moderator", "Supporter"});
		PERMS.put("playMusic", new String[] {"*"});
		PERMS.put("vote", new String[] {"*"});
		PERMS.put("vote.create", new String[] {"*"});
		PERMS.put("vote.vote", new String[] {"*"});
		PERMS.put("vote.close", PERMS.get("vote.create"));
		PERMS.put("vote.stats", PERMS.get("vote.vote"));
		PERMS.put("prefix", new String[] {"*"});
		PERMS.put("prefix.set", new String[] {"Owner", "Admin"});
		PERMS.put("prefix.show", new String[] {"*"});
		PERMS.put("autoChannel", new String[] {"Owner", "Admin"});
		PERMS.put("stop", new String[] {"Owner", "Admin"});
		PERMS.put("restart", new String[] {"Owner", "Admin"});
		PERMS.put("perm", new String[] {"*"});
		PERMS.put("perm.get", PERMS.get("perm"));
		PERMS.put("perm.change", new String[] {"Owner"});
		PERMS.put("kick", new String[] {"Owner"});
		PERMS.put("ban", new String[] {"Owner"});
		PERMS.put("role", new String[] {"Owner"});
		PERMS.put("spam", new String[] {"Owner"});
		PERMS.put("logger", new String[] {"*"});
		PERMS.put("logger.show", new String[] {"*"});
		PERMS.put("logger.set", new String[] {"Owner", "Admin"});
		PERMS.put("userinfo", new String[] {"*"});
		PERMS.put("autorole", new String[] {"Owner", "Admin"});
		PERMS.put("unnick.others", new String[] {"Owner", "Admin", "Moderator", "Supporter"});
		PERMS.put("unnick", new String[] {"*"});
		PERMS.put("multicolor.set", new String[] {"Owner", "Admin"});
		PERMS.put("dice", new String[] {"*"});
		PERMS.put("vkick", new String[] {"Owner", "Admin", "Moderator", "Supporter"});
		
		markupEscapeMap.put("\\*", "\\*");//fett/kursiv
		markupEscapeMap.put("_", "\\_");//unterstrichen
		markupEscapeMap.put("~", "\\~");//durchgestrichen
		markupEscapeMap.put("`", "\\`");//Code/Codeblock
		markupEscapeMap.put("^", "\\^");//hochgestellt
		markupEscapeMap.put("%", "\\%");//"smallcaps"
		markupEscapeMap.put("#", "\\#");//fullwidth
		markupEscapeMap.put("&", "\\&");//upsidedown
		markupEscapeMap.put("|", "\\|");//varied
	}
	/**
	 * sends an Error Message
	 * @param channel The {@link TextChannel} where the Message should be sent
	 * @param text The text of the Message as {@link String}
	 */
	public static void errmsg(TextChannel channel, String text) {
		msg(channel, text, Color.RED, true);
	}
	/**
	 * send a Message
	 * standardColor: {@link Color#GREEN}
	 * @param channel The {@link TextChannel} where the Message should be sent
	 * @param text The text of the Message as {@link String}
	 */
	public static void msg(TextChannel channel, String text) {
		msg(channel, text, Color.GREEN, false);
	}
	/**
	 * send a Message
	 * standardColor: {@link Color#GREEN}
	 * @param channel The {@link TextChannel} where the Message should be sent
	 * @param text The text of the Message as {@link String}
	 * @param timeout should the Message be deleted automatically
	 */
	public static void msg(TextChannel channel, String text,boolean timeout) {
		msg(channel, text, Color.GREEN, timeout);
	}
	/**
	 * send a Message
	 * @param channel The {@link TextChannel} where the Message should be sent
	 * @param text The text of the Message as {@link String}
	 * @param color the {@link Color} of the Message
	 * @param timeout should the Message be deleted automatically
	 */
	public static void msg(TextChannel channel, String text,Color color,boolean timeout) {
		try {
			Message msg=channel.sendMessage(
					new EmbedBuilder()
					.setColor(color)
					.setDescription(text)
					.build()).complete();
			if (timeout) {
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						msg.delete().queue();
						
					}
				}, STATIC.INFO_TIMEOUT);
			}
		} catch (Exception e) {
			System.err.println("Cannot send Message \""+text+"\" in channel "+channel.getName()+"["+channel.getGuild().getName()+"]");
		}
	}
	/**
	 * Method to escape Discord Formattings
	 * @param unescaped the unescaped Text
	 * @return The escaped Text
	 */
	public static String escapeDiscordMarkup(String unescaped) {
		for (String toEscape : markupEscapeMap.keySet()) {
			unescaped=unescaped.replace(toEscape, markupEscapeMap.get(toEscape));
		}
		return unescaped;
	}
	/**
	 * sucht {@link Role}s in einer {@link Message}
	 * @param msg die zu durchsuchende Nachricht
	 * @return die Rollen
	 */
	public static List<Role> getRolesFromMsg(Message msg){
		String[] args=msg.getContentRaw().split(" ");
		List<Role> roles=new ArrayList<>();
		roles.addAll(msg.getMentionedRoles());
		
		for (int i = 1; i < args.length; i++) {
			Role role=null;
			
			try {
				role=msg.getGuild().getRoleById(args[i]);
			} catch (NumberFormatException e) {
				
			}
			
			if (role==null) {
				List<Role> rolesLocal=msg.getGuild().getRolesByName(args[i], true);
				if (!rolesLocal.isEmpty()) {
					role=rolesLocal.get(0);
				}
			}
			if (role!=null) {
				roles.add(role);
			}
			
		}
		return roles;
	}
	/**
	 * gets {@link Member}s from a {@link Message}
	 * @param msg the Message
	 * @return a {@link List} of {@link Member}s
	 */
	public static List<Member> getMembersFromMsg(Message msg){
		String[] args=msg.getContentRaw().split(" ");
		List<Member> members=new ArrayList<>();
		for (User user : msg.getMentionedUsers()) {
			members.add(msg.getGuild().getMember(user));
		}
		
		for (int i = 1; i < args.length; i++) {
			Member member=null;
			
			try {
				member=msg.getGuild().getMemberById(args[i]);
			} catch (NumberFormatException e) {
				
			}
			
			if (member==null) {
				List<Member> membersLocal=msg.getGuild().getMembersByEffectiveName(args[i], true);
				if (!membersLocal.isEmpty()) {
					member=membersLocal.get(0);
				}
			}
			
			members.add(member);
		}
		return members;
	}
	/**
	 * gets Bot Prefix for a specified Guild
	 * @param g The Guild(Discord-Server)
	 * @return the Prefix of the Guild
	 */
	public static String getPrefix(final Guild g) {
		final String prefix=prefixe.get(g);
		if(prefix==null) {
			return PREFIX;
		}
		return prefix;
	}
	/**
	 * gets the prefix of a Guild and escapes it
	 * @param g the {@link Guild}
	 * @return the escaped prefix
	 */
	public static String getPrefixExcaped(final Guild g) {
		return escapeDiscordMarkup(getPrefix(g));
	}
	/**
	 * gets guild-local Permission info using a specified permission name
	 * @param g The {@link Guild} from the Permission
	 * @param permName The name of the Permission
	 * @return A String[] of the Role names which have the Permission
	 */
	public static String[] getPerm(Guild g, String permName) {
		
		try {
			//loadPerms(g);
			if(permsLocal.get(g)!=null) {
				if (permsLocal.get(g).get(permName)==null) {
					permsLocal.get(g).put(permName, new String[] {""});
				}
				return permsLocal.get(g).get(permName);
			}
		} catch (Exception e) {
		}
		
		return PERMS.get(permName);
	}
	/**
	 * gets the Permissions of a {@link Guild}
	 * @param g The Guild(Discord-Server)
	 * @return A {@link Map} of the Permissions
	 */
	public static Map<String, String[]> getPerms(Guild g) {
		
		try {
			//loadPerms(g);
			if(permsLocal.get(g)!=null) {
				return permsLocal.get(g);
			}
		} catch (Exception e) {}
		
		return PERMS;
	}
	/**
	 * resets the Permissions for a Guild
	 * @param g The Guild(Discord-Server)
	 */
	public static void resetPerms(Guild g) {
		permsLocal.put(g, PERMS);
		savePerms(g);
	}
	/**
	 * deletes a <b>userdefined</b> Permission in a {@link Guild}
	 * @param g The Guild(Discord-Server)
	 * @param permName The name of the Permission which should be deleted
	 * @return true if something changed
	 */
	public static boolean removePerm(Guild g, String permName) {
		if (permsLocal.get(g).containsKey(permName)) {
			if (PERMS.containsKey(permName)) {
				return false;
			}
			permsLocal.get(g).remove(permName);
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
		//loadPerms(g);
		for (String[] perm : permsLocal.get(g).values()) {
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
		HashMap<String, String[]> perms=permsLocal.get(g);
		if (perms==null) {
			permsLocal.put(g, PERMS);
			return;
		}
		for (String permName : PERMS.keySet()) {
			if (!(perms.containsKey(permName))) {
				perms.put(permName, PERMS.get(permName));
			}
		}
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
			//loadPerms(g);
			if(g!=null) {
				
				if (!permsLocal.containsKey(g)) {
					permsLocal.put(g, PERMS);
					
				}
				permsLocal.get(g).put(permName, perm);
				
				int num=0;
				for (String string : permsLocal.get(g).get(permName)) {
					if(string!=null) {
						num++;
					}
				}
				String[] permsNew=new String[num];
				
				for (int i = 0,j=0; i < perm.length; i++) {
					if (perm[i]!=null) {
						permsNew[j]=perm[i];
						perm[i]=null;
						j++;
						continue;
					}
				}
				permsLocal.get(g).put(permName, permsNew);
				savePerms(g);
			}
		} catch (Exception e) {}
	}
	/**
	 * Loads data of all Guilds
	 * @param jda The JDA Instance
	 */
	public static void loadData(JDA jda) {
		for (Guild guild : jda.getGuilds()) {
			loadPrefix(guild);
			loadPerms(guild);
			
		}
		loadCmdLogger();
		MultiColorChanger.loadRoles(jda);
	}
	/**
	 * sets the Bot prefix for a Guild
	 * @param g The Guild(Discord-Server)
	 * @param prefix the prefix to set
	 */
	public static void setPrefix(final Guild g, final String prefix) {
		prefixe.put(g, prefix);
		savePrefix(g);
	}
	/**
	 * saves the Guild prefix to a File
	 * @param guild the Guild(Discord-Server)
	 */
	private static void savePrefix(final Guild guild){
		if (!new File(STATIC.getSettingsDir()+"/"+guild.getId()).exists()) {
			new File(STATIC.getSettingsDir()+"/"+guild.getId()).mkdirs();
		}
		final String saveFile=STATIC.getSettingsDir()+"/"+guild.getId()+"/prefix.dat";
		if(!prefixe.containsKey(guild)) {
			final File f=new File(saveFile);
			f.delete();
			return;
		}
		
		
		File file=new File(saveFile);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		
		final String prefix=prefixe.get(guild);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			final ObjectOutputStream oos=new ObjectOutputStream(fos);
			oos.writeObject(prefix);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * loads the prefix from a Guild
	 * @param g The Guild(Discord-Server)
	 */
	private static void loadPrefix(final Guild g) {
		if (!new File(STATIC.getSettingsDir()+"/"+g.getId()).exists()) {
			new File(STATIC.getSettingsDir()+"/"+g.getId()).mkdirs();
		}
		final File file=new File(STATIC.getSettingsDir()+"/"+g.getId()+"/prefix.dat");
		if (!file.exists()) {
			
		}
		else {
			try {
				final FileInputStream fis=new FileInputStream(file);
				final ObjectInputStream ois=new ObjectInputStream(fis);
				prefixe.put(g, (String) ois.readObject());
				ois.close();
			} catch (IOException|ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * saves the Permissions of a {@link Guild}
	 * @param guild The Guild(Discord-Server)
	 */
	private static void savePerms(final Guild guild){
		
		if (!new File(STATIC.getSettingsDir()+"/"+guild.getId()).exists()) {
			new File(STATIC.getSettingsDir()+"/"+guild.getId()).mkdirs();
		}
		
		final String saveFile=STATIC.getSettingsDir()+"/"+guild.getId()+"/perms.dat";
		File file=new File(saveFile);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if(!permsLocal.containsKey(guild)) {
			final File f=new File(saveFile);
			f.delete();
			return;
		}
		
		final HashMap<String, String[]> perms=permsLocal.get(guild);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(saveFile);
			final ObjectOutputStream oos=new ObjectOutputStream(fos);
			oos.writeObject(perms);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Loads the Permissions of a {@link Guild}
	 * @param g The Guild(Discord-Server)
	 */
	@SuppressWarnings("unchecked")
	private static void loadPerms(final Guild g) {
		if (!new File(STATIC.getSettingsDir()+"/"+g.getId()).exists()) {
			new File(STATIC.getSettingsDir()+"/"+g.getId()).mkdirs();
		}
		final File file=new File(STATIC.getSettingsDir()+"/"+g.getId()+"/perms.dat");
		if (!file.exists()) {
			if (!permsLocal.containsKey(g)) {
				permsLocal.put(g, PERMS);
				savePerms(g);
			}
			return;
		}
		
		else {
			try {
				final FileInputStream fis=new FileInputStream(file);
				final ObjectInputStream ois=new ObjectInputStream(fis);
				permsLocal.put(g, (HashMap<String, String[]>) ois.readObject());
				ois.close();
			} catch (IOException|ClassNotFoundException e) {
				e.printStackTrace();
			}
			if (!permsLocal.containsKey(g)) {
				permsLocal.put(g, PERMS);
				savePerms(g);
			}
		}
	}
	/**
	 * gets the name of the Logger Channel for a {@link Guild}
	 * @param g The Guild(Discord-Server)
	 * @return The name of Logger-Channels of the Guild
	 */
	public static String getCmdLogger(Guild g) {
		if (cmdLoggerNames.containsKey(g.getId())) {
			return cmdLoggerNames.get(g.getId());
		}
		return STD_CMD_LOGGER_NAME;
	}
	/**
	 * sets the name of the Logger Channel for a {@link Guild}
	 * @param g The Guild(Discord-Server)
	 * @param channel Der Name d. Logger-Channels d. Guild
	 */
	public static void setCmdLogger(Guild g, String channel) {
		loadCmdLogger();
		cmdLoggerNames.put(g.getId(), channel);
		saveCmdLogger();
	}
	/**
	 * saves the Logger Channels in a File
	 */
	private static void saveCmdLogger() {
		save("cmdLogger.dat", cmdLoggerNames);
	}
	/**
	 * gets data from a {@link Guild}
	 * @param g The {@link Guild}(Discord-Server)
	 * @return data as a {@link String}
	 */
	public static String getServerData(Guild g){
		String retString=g.getName()+" ["+g.getId()+"]";
		return retString+"\t"+getActiveInvite(g);
		
	}
	/**
	 * gets an {@link Invite} in a Guild
	 * @param g The {@link Guild} where an {@link Invite} should be loaded
	 * @return the URL of the {@link Invite} or <code>null</code> if there is no {@link Invite}
	 */
	public static String getActiveInvite(Guild g) {
		
		try {		
			for (Invite inv : g.getInvites().complete()) {
				if (!inv.isExpanded()) {
					return inv.getURL();
				}
			}
			
		} catch (InsufficientPermissionException e) {
			try {
				for (TextChannel channel : g.getTextChannels()) {
					for (Invite inv : channel.getInvites().complete()) {
						if (!inv.isExpanded()) {
							return inv.getURL();
						}
					}
				}
				for (VoiceChannel channel : g.getVoiceChannels()) {
					for (Invite inv : channel.getInvites().complete()) {
						if (!inv.isExpanded()) {
							return inv.getURL();
						}
					}
				}
			} catch (InsufficientPermissionException e2) {
			}
			
		}
		
		return "";
	}
	/**
	 * gets an {@link Invite} in a Guild or creates one if there is none
	 * @param g The {@link Guild} where an {@link Invite} should be loaded
	 * @return the URL of the {@link Invite}
	 */
	public static String createInvite(Guild g) {
		
		try {		
			for (Invite inv : g.getInvites().complete()) {
				if (!inv.isExpanded()) {
					return inv.getURL();
				}
			}
		} catch (Exception e) {}
		try {
			return g.getDefaultChannel().createInvite().complete().getURL();
		} catch (Exception e) {}
		for (TextChannel channel : g.getTextChannels()) {
			try {
				return channel.createInvite().setMaxAge(60).complete().getURL();
			} catch (Exception e) {}
		}
		
		for (VoiceChannel channel : g.getVoiceChannels()) {
			try {
				return channel.createInvite().setMaxAge(60).complete().getURL();
			} catch (Exception e) {}
		}
		return "";
	}
	/**
	 * Loads the Loggerchannels from a File
	 */
	@SuppressWarnings("unchecked")
	private static void loadCmdLogger() {
		Object names=load("cmdLogger.dat");
		if (names != null) {
			cmdLoggerNames= (HashMap<String, String>)names;
		}
		cmdLoggerNames=new HashMap<>();
		//return (HashMap<String, String>)load("cmdLogger.dat");
	}
	/**
	 * saves an Object into a File
	 * @param filename The name of the File
	 * @param toSave The Object to save(should be {@link Serializable})
	 */
	public static void save(String filename, Object toSave) {
		final File file=new File(STATIC.getSettingsDir()+"/"+filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			final FileOutputStream fos=new FileOutputStream(file);
			final ObjectOutputStream oos=new ObjectOutputStream(fos);
			oos.writeObject(toSave);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Loads an Object from a File
	 * @param filename The name of the file
	 * @return The Object to load
	 */
	public static Object load(String filename) {
		Object o=null;
		final File file=new File(STATIC.getSettingsDir()+"/"+filename);
		if (file.exists()) {
			try {
				final FileInputStream fis=new FileInputStream(file);
				final ObjectInputStream ois=new ObjectInputStream(fis);
				o=ois.readObject();	
				ois.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return o;
	}
	/**
	 * gets the Path for Files to save/load and creates it (if nessecery)
	 * @return the Path for Botdata Files
	 */
	public static String getSettingsDir() {
		File dir=new File(SETTINGS_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return SETTINGS_DIR;
	}
}
