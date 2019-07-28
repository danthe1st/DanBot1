package io.github.danthe1st.danbot1.util;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import io.github.danthe1st.danbot1.commands.admin.CmdBlacklist;
import io.github.danthe1st.danbot1.commands.botdata.CmdAutoChannel;
import io.github.danthe1st.danbot1.commands.botdata.CmdVote;
import io.github.danthe1st.danbot1.commands.moderation.ban.AutoUnbanner;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.listeners.AutoRoleListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

/**
 * Class for Constants, data and utility-methods
 * @author Daniel Schmid
 */
public final class STATIC {
	private STATIC(){}
	private static HashMap<Guild, String> prefixes=new HashMap<>();
	private static final String PREFIX="--";
	
	public static final String VERSION="v4.0 - Living";
	private static String settingsDir="./DANBOT1_SETTINGS";
	public static final String AUTOCHANNEL_POSTFIX=" [Autochannel]";
	public static final int INFO_TIMEOUT=5000;
	private static final String STD_CMD_LOGGER_NAME="cmdLog";
	private static HashMap<String, String> cmdLoggerNames =new HashMap<>();
	private static final Map<String, String> markupEscapeMap=new HashMap<>();
	
	private static final String DIR_CREATION_FAILED="cannot create directory: ";
	/**
	 * initialize permissions and markup escapes
	 */
	static {
		markupEscapeMap.put("*", "\\*");//bold/italic
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
		msg(channel, new EmbedBuilder()
					.setColor(color)
					.setDescription(text)
					.build(), timeout);
	}
	/**
	 * send a Message
	 * @param channel The {@link TextChannel} where the Message should be sent
	 * @param message The content of the Message as {@link MessageEmbed}
	 * @param timeout should the Message be deleted automatically
	 */
	public static void msg(TextChannel channel, MessageEmbed message,boolean timeout) {
		try {
			Message msg=channel.sendMessage(
					message).complete();
			if (timeout) {
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						try {
							msg.delete().queue();
						} catch (IllegalArgumentException e) {
							//continue with execution
						}
					}
				}, STATIC.INFO_TIMEOUT);
			}
		} catch (Exception e) {
			System.err.println("Cannot send Message \""+message.getDescription()+"\" in channel "+channel.getName()+"["+channel.getGuild().getName()+"] because of a "+e.getClass().getSimpleName());
		}
	}
	/**
	 * Method to escape Discord Formattings
	 * @param unescaped the unescaped Text
	 * @return The escaped Text
	 */
	public static String escapeDiscordMarkup(String unescaped) {
		for (Entry<String, String> entry : markupEscapeMap.entrySet()) {
			unescaped=unescaped.replace(entry.getKey(), entry.getValue());
		}
		
		return unescaped;
	}
	/**
	 * gets all {@link Role}s in a {@link Message}
	 * @param msg the Message to search
	 * @return the roles
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
				//continue with execution
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
	public static Set<Member> getMembersFromMsg(Message msg){
		String[] args=msg.getContentRaw().split(" ");
		Set<Member> members=new HashSet<>();
		for (User user : msg.getMentionedUsers()) {
			members.add(msg.getGuild().getMember(user));
		}
		for (int i = 1; i < args.length; i++) {
			Member member=null;
			try {
				member=msg.getGuild().getMemberById(args[i]);
			} catch (NumberFormatException e) {
				//continue with execution
			}
			if (member==null) {
				List<Member> membersLocal=msg.getGuild().getMembersByEffectiveName(args[i], true);
				if (membersLocal.isEmpty()) {
					membersLocal=msg.getGuild().getMembersByName(args[i], true);
				}
				if (!membersLocal.isEmpty()) {
					member=membersLocal.get(0);
				}
			}
			if (member!=null) {
				members.add(member);
			}
		}
		return members;
	}
	/**
	 * gets Bot Prefix for a specified Guild
	 * @param g The Guild(Discord-Server)
	 * @return the Prefix of the Guild
	 */
	public static String getPrefix(final Guild g) {
		final String prefix=prefixes.get(g);
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
	public static String getPrefixEscaped(final Guild g) {
		return escapeDiscordMarkup(getPrefix(g));
	}
	/**
	 * Loads data of all Guilds
	 * @param jda The JDA Instance
	 */
	public static void loadData(JDA jda) {
		CmdAutoChannel.load(jda);
		CmdVote.loadPolls(jda);
		CmdBlacklist.loadBlacklist();
		for (Guild guild : jda.getGuilds()) {
			loadPrefix(guild);
			PermsCore.loadPerms(guild);
			AutoRoleListener.load(guild);
		}
		loadCmdLogger();
		AutoUnbanner.loadUnBans(jda);
		LanguageController.load(jda);
	}
	/**
	 * sets the Bot prefix for a Guild
	 * @param g The Guild(Discord-Server)
	 * @param prefix the prefix to set
	 */
	public static void setPrefix(final Guild g, final String prefix) {
		prefixes.put(g, prefix);
		savePrefix(g);
	}
	/**
	 * saves the Guild prefix to a File
	 * @param guild the Guild(Discord-Server)
	 */
	private static void savePrefix(final Guild guild){
		File dir=new File(STATIC.getSettingsDir()+"/"+guild.getId());
		if (!(dir.exists()||dir.mkdirs())) {
			System.err.println(DIR_CREATION_FAILED+dir.getAbsolutePath());
		}
		final String saveFile=STATIC.getSettingsDir()+"/"+guild.getId()+"/prefix.dat";
		if(!prefixes.containsKey(guild)) {
			final File f=new File(saveFile);
			if(!f.exists()) {
				try {
					Files.delete(f.toPath());
				} catch (IOException e) {
					System.err.println("cannot delete file: "+f.getAbsolutePath());
				}
			}
			return;
		}
		File file=new File(saveFile);
		if (!file.exists()) {
			try {
				Files.createFile(file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		final String prefix=prefixes.get(guild);
		try (final ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(file))){
			oos.writeObject(prefix);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * loads the prefix from a Guild
	 * @param g The Guild(Discord-Server)
	 */
	private static void loadPrefix(final Guild g) {
		File dir=new File(STATIC.getSettingsDir()+"/"+g.getId());
		if (!(dir.exists()||dir.mkdirs())) {
			System.err.println(DIR_CREATION_FAILED+dir.getAbsolutePath());
		}
		final File file=new File(dir,"/prefix.dat");
		if (file.exists()) {
			try (final ObjectInputStream ois=new ObjectInputStream(new FileInputStream(file))){
				prefixes.put(g, (String) ois.readObject());
			} catch (IOException|ClassNotFoundException e) {
				e.printStackTrace();
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
		return g.getName()+" ["+g.getId()+"]";
	}
	/**
	 * gets an {@link Invite} in a Guild
	 * @param g The {@link Guild} where an {@link Invite} should be loaded
	 * @return the URL of the {@link Invite} or <code>null</code> if there is no {@link Invite}
	 */
	public static String getActiveInvite(Guild g) {
		try {		
			List<Invite> invs=g.retrieveInvites().complete();
			if (!invs.isEmpty()) {
				return invs.get(0).getUrl();
			}
		} catch (InsufficientPermissionException e) {
			try {
				for (TextChannel channel : g.getTextChannels()) {
					List<Invite> invs = channel.retrieveInvites().complete();
					if (!invs.isEmpty()) {
						return invs.get(0).getUrl();
					}
				}
				for (VoiceChannel channel : g.getVoiceChannels()) {
					List<Invite> invs = channel.retrieveInvites().complete();
					if (!invs.isEmpty()) {
						return invs.get(0).getUrl();
					}
				}
			} catch (InsufficientPermissionException e2) {
				//continue with execution
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
		if (g==null) {
			return null;
		}
		String invite=getActiveInvite(g);
		if (!invite.equals("")) {
			return invite;
		}
		return createInvite(g,0);
	}
	/**
	 * creates an {@link Invite} in a Guild.
	 * @param g The {@link Guild} where an {@link Invite} should be loaded
	 * @param maxAge the max age of the invite.
	 * @return the URL of the {@link Invite}
	 */
	private static String createInvite(Guild g,int maxAge) {
		if (g==null) {
			return null;
		}
		try {
			return g.getDefaultChannel().createInvite().setMaxAge(maxAge).complete().getUrl();
		} catch (Exception e) {
			//continue with execution
		}
		for (TextChannel channel : g.getTextChannels()) {
			try {
				return channel.createInvite().setMaxAge(maxAge).complete().getUrl();
			} catch (Exception e) {
				//continue with execution
			}
		}
		for (VoiceChannel channel : g.getVoiceChannels()) {
			try {
				return channel.createInvite().setMaxAge(maxAge).complete().getUrl();
			} catch (Exception e) {
				//continue with execution
			}
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
				Files.createFile(file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try (final ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(file))){
			oos.writeObject(toSave);
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
			try (final ObjectInputStream ois=new ObjectInputStream(new FileInputStream(file))){
				o=ois.readObject();	
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
		File dir=new File(settingsDir);
		if (!(dir.exists()||dir.mkdirs())) {
			System.err.println(DIR_CREATION_FAILED+dir.getAbsolutePath());
		}
		return settingsDir;
	}
	/**
	 * sets the Path for Files to save/load and creates it (if nessecery)
	 * @param directory the Path for Botdata Files
	 */
	public static void setSettingsDir(String directory) {
		if (directory==null) {
			return;
		}
		File dir=new File(directory);
		if (!(dir.exists()||dir.mkdirs())) {
			System.err.println(DIR_CREATION_FAILED+dir.getAbsolutePath());
		}
		settingsDir=directory;
	}
}
