package util;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

/**
 * Klasse f. Konstanten, Daten und util-Methoden<br>
 * <b>KEINE INSTANZ ODER VERERBUNG SOLL M�GLICH SEIN!!!</b>
 * @author Daniel Schmid
 *
 */
public final class STATIC {
	/**
	 * Konstruktor soll unsichtbar sein
	 */
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
	 * Initialisiere PERMS
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
	 * sendet eine Fehlernachricht
	 * @param channel Der {@link TextChannel} in dem die Nachricht gesendet werden soll
	 * @param text Der Text der Nachricht(als {@link String})
	 */
	public static void errmsg(TextChannel channel, String text) {
		msg(channel, text, Color.RED, true);
	}
	/**
	 * sende eine Nachricht<br>
	 * standardfarbe: {@link Color#GREEN}
	 * @param channel Der {@link TextChannel} in dem die Nachricht gesendet werden soll
	 * @param text Der Text der Nachricht(als {@link String})
	 */
	public static void msg(TextChannel channel, String text) {
		msg(channel, text, Color.GREEN, false);
	}
	/**
	 * sende eine Nachricht<br>
	 * standardfarbe: {@link Color#GREEN}
	 * @param channel Der {@link TextChannel} in dem die Nachricht gesendet werden soll
	 * @param text Der Text der Nachricht(als {@link String})
	 * @param timeout soll die Nachricht automatisch gelöscht werden?
	 */
	public static void msg(TextChannel channel, String text,boolean timeout) {
		msg(channel, text, Color.GREEN, timeout);
	}
	/**
	 * sende eine Nachricht<br>
	 * @param channel Der {@link TextChannel} in dem die Nachricht gesendet werden soll
	 * @param text Der Text der Nachricht(als {@link String})
	 * @param color Die Farbe der Nachricht({@link MessageEmbed})
	 * @param timeout soll die Nachricht automatisch gelöscht werden?
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
	 * escaped Discord Formatierungen
	 * @param unescaped der Text mit Formatierungen
	 * @return Der Text mit escapten Formatierungen
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
			
			roles.add(role);
		}
		return roles;
	}
	/**
	 * sucht {@link Member}s in einer {@link Message}
	 * @param msg die zu durchsuchende Nachricht
	 * @return die Members
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
	 * Getter f. Prefix einer Guild
	 * @param g Die Guild(Discord-Server)
	 * @return der Pr�fix f. d. Guild
	 */
	public static String getPrefix(final Guild g) {
		//loadPrefix(g);
		final String prefix=prefixe.get(g);
		if(prefix==null) {
			return PREFIX;
		}
		return prefix;
	}
	public static String getPrefixExcaped(final Guild g) {
		return escapeDiscordMarkup(getPrefix(g));
	}
	/**
	 * Getter f. permission innerhalb einer Guild mithilfe des Permissionnamens
	 * @param g Die Guild d. gesuchten Permission
	 * @param permName Der name der ges. Permission
	 * @return Die Gruppen, die die Permissions haben
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
	 * Gibt eine java.util.Map mit den Permissions einer Guild zur�ck
	 * @param g Die Guild(Discord-Server)
	 * @return die Permissions
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
	 * resettet die Permissions f. eine Guild
	 * @param g Die Guild(Discord-Server)
	 */
	public static void resetPerms(Guild g) {
		permsLocal.put(g, PERMS);
		savePerms(g);
	}
	/**
	 * entfernt eine <b>benutzerdefinierte</b> Permission
	 * @param g Die Guild(Discord-Server)
	 * @param permName Der Name der Permission
	 * @return erfolgreich?
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
	 * sucht alle F�lle wo diese Rolle vorkommt und �ndert sie
	 * @param g Die Guild(Discord-Server)
	 * @param roleToChange Die Rolle, die die Permissions hat
	 * @param newRole Die Rolle, die die Permissions haben soll
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
	 * Lädt eventuell neue Permissions(durch Update) in die Guild
	 * @param g
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
	 * setzt eine Permission in einer Guild
	 * @param g Die Guild(Discord-Server)
	 * @param permName Der Name d. Permission
	 * @param perm Wer hat die Permission(Rollen)
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
	 * Lädt die Daten der Guilds
	 * @param jda Die JDA
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
	 * setze Prefix f. eine Guild auf bestimmten Wert
	 * @param g Die Guild(Discord-Server)
	 * @param prefix Der zu setzende Prefix
	 */
	public static void setPrefix(final Guild g, final String prefix) {
		prefixe.put(g, prefix);
		savePrefix(g);
	}
	/**
	 * speichert den Prefix einer Guild in einer Datei
	 * @param guild die Guild(Discord-Server)
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
	 * Lädt den Prefix einer Guild
	 * @param g Die Guild(Discord-Server)
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
	 * speichert die Permissions einer Guild
	 * @param guild Die Guild(Discord-Server)
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
	 * Lädt die Permissions einer Guild
	 * @param g Die Guild(Discord-Server)
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
	 * Getter g. Loggerchannel-Name
	 * @param g Die Guild(Discord-Server)
	 * @return Der Name d. Logger-Channels d. Guild
	 */
	public static String getCmdLogger(Guild g) {
		if (cmdLoggerNames.containsKey(g.getId())) {
			return cmdLoggerNames.get(g.getId());
		}
		return STD_CMD_LOGGER_NAME;
	}
	/**
	 * Setter g. Loggerchannel-Name
	 * @param g Die Guild(Discord-Server)
	 * @param channel Der Name d. Logger-Channels d. Guild
	 */
	public static void setCmdLogger(Guild g, String channel) {
		loadCmdLogger();
		cmdLoggerNames.put(g.getId(), channel);
		saveCmdLogger();
	}
	/**
	 * speichert die Loggerchannels in eine Datei
	 */
	private static void saveCmdLogger() {
		save("cmdLogger.dat", cmdLoggerNames);
	}
	/**
	 * lädt Daten zu einer {@link Guild}
	 * @param g die {@link Guild} (Discord-Server)
	 * @return Serverdaten als {@link String}
	 */
	public static String getServerData(Guild g){
		String retString=g.getName()+" ["+g.getId()+"]";
		return retString+"\t"+getActiveInvite(g);
		
	}
	/**
	 * Sucht eine {@link Invite} in einer {@link Guild}
	 * @param g die {@link Guild} in der nach einer {@link Invite} gesucht werden soll
	 * @return die URL der {@link Invite} oder <code>null</code> falls keine Invite vorhanden ist.
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
	 * Sucht eine {@link Invite} in einer {@link Guild} oder erstellt eine, falls keine vorhanden ist
	 * @param g die {@link Guild} in der nach einer {@link Invite} gesucht werden soll
	 * @return die URL der {@link Invite}
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
			return g.getDefaultChannel().createInvite().setMaxAge(60).complete().getURL();
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
	 * Lädt die Loggerchannels aus einer Datei
	 * @return
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
	 * speichert ein Objekt in einer Datei
	 * @param filename Der Name der Datei
	 * @param toSave Das Object, das zu sichern ist
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
	 * lädt ein Objekt aus einer Datei
	 * @param filename Der Name der Datei
	 * @return Das Object, das zu laden ist
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
	 * gibt den Speicherpfad zurück bzw. erstellt diesen(falls nötig)
	 * @return der Pfad, wo Daten gespeichert werden sollen
	 */
	public static String getSettingsDir() {
		File dir=new File(SETTINGS_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return SETTINGS_DIR;
	}
}
