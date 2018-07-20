package commands.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import commands.Command;
import core.PermsCore;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Klasse f. Command v. Autochannel-Feature
 * @author Daniel Schmid
 *
 */
public class CmdAutoChannel implements Command, Serializable {
	private static final long serialVersionUID = 1L;
	
	private static HashMap<VoiceChannel, Guild> autoChannels=new HashMap<>();
	
	public static HashMap<VoiceChannel, Guild> getAutoChannels(){
		return autoChannels;
	}
	public static VoiceChannel getVoiceChannel(final String id,final Guild g) {
		return g.getVoiceChannelById(id);
	}
	private static Guild getGuild(final String id, final JDA jda) {
		return jda.getGuildById(id);
	}
	/**
	 * setzt einen neuen autochannel
	 * @param id ID des autochannels
	 * @param g Die Guild(der Discord-server)
	 * @param tc Der Discord-Textkanal
	 */
	private void setChannel(final String id, final Guild g, final TextChannel tc) {
		final VoiceChannel vc=getVoiceChannel(id, g);
		if (vc==null) {
			
			STATIC.errmsg(tc, "Please enter a valid channel ID!");
		}
		else if(autoChannels.containsKey(vc)) {
			STATIC.errmsg(tc, "This channel is still registered as a auto channel.");
		}
		else {
			autoChannels.put(vc, g);
			save();
			STATIC.msg(tc, String.format("Successfully set channel \'%s\' as an auto channel", vc.getName()));
		}
		
	}
	/**
	 * l�scht einen vorhandenen autochannel
	 * @param id ID des autochannels
	 * @param g Die Guild(der Discord-server)
	 * @param tc Der Discord-Textkanal
	 */
	private void unsetChan(final String id, final Guild g, final TextChannel tc) {
		final VoiceChannel vc=getVoiceChannel(id, g);
		if(vc==null) {
			STATIC.errmsg(tc, "Please enter a valid channel ID!");
		}
		else if (!autoChannels.containsKey(vc)) {
			STATIC.errmsg(tc, "This channel is not set as an auto channel");
		}
		else {
			autoChannels.remove(vc);
			save();
			STATIC.msg(tc, String.format("Successfully unset autoChannel \'%s.\'", vc.getName()));
		}
	}
	/**
	 * l�scht einen vorhandenen autochannel
	 * @param vc Der VoiceChannel
	 */
	public static void unsetChan(final VoiceChannel vc) {
		autoChannels.remove(vc);
		save();
	}
	/**
	 * schreibt Nachricht mit allen autochannels in den �bergegebenen Textkanal
	 * @param g Guild(Discord Server)
	 * @param tc Der Textkanal
	 */
	private void listChans(final Guild g, final TextChannel tc) {
		final StringBuilder sb=new StringBuilder().append("**AUTO CHANNELS:\n\n**");
		autoChannels.keySet().stream()
			.filter(vc->autoChannels.get(vc).equals(g))
			.forEach(vc->sb.append(String.format(":white_small_square: \'%s\' *(%s)\n", vc.getName(), vc.getId())));
		
		STATIC.msg(tc, sb.toString());
	}
	/**
	 * speichert die autochannels
	 */
	private static void save() {
		final File path=new File(STATIC.getSettingsDir());
		if (!path.exists()) {
			path.mkdir();
		}
		final HashMap<String, String> out=new HashMap<>();
		autoChannels.forEach((vc,g)->out.put(vc.getId(), g.getId()));
		try {
			final FileOutputStream fos=new FileOutputStream(STATIC.getSettingsDir()+"/autochannels.dat");
			final ObjectOutputStream oos=new ObjectOutputStream(fos);
			oos.writeObject(out);
			oos.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * L�dt die autochannels
	 * @param jda
	 */
	public static void load(final JDA jda) {
		
		final File file=new File(STATIC.getSettingsDir()+"/autochannels.dat");
		if (!file.exists()) {
			return;
		}
		try {
			final FileInputStream fis=new FileInputStream(file);
			final ObjectInputStream ois=new ObjectInputStream(fis);
			@SuppressWarnings("unchecked")
			final HashMap<String, String> out=(HashMap<String, String>)ois.readObject();
			ois.close();
			out.forEach((vId, gId)->{
				final Guild g=getGuild(gId, jda);
				try {
					autoChannels.put(getVoiceChannel(vId, g), g);
				} catch (Exception e) {
				}
			});
			
		} catch (IOException|ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}

	/**
	 * Der Befehl selbst(siehe help)
	 */
	@Override
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(!PermsCore.check(event, "autoChannel")) {
			return;
		}
		final Guild g=event.getGuild();
		final TextChannel tc=event.getTextChannel();
		if(args.length<1) {
			STATIC.errmsg(tc, help(STATIC.getPrefixExcaped(g)));
			
			return;
		}
		switch (args[0]) {
		case "list":
			listChans(g, tc);
			break;
		case "set":
		case "add":
			if(args.length<2) {
				STATIC.errmsg(tc, help(STATIC.getPrefixExcaped(g)));
				return;
			}
			setChannel(args[1], g, tc);
			break;
		case "remove":
		case "unset":
		case "rem":
		case "delete":
		case "del":
			if(args.length<2) {
				STATIC.errmsg(tc, help(STATIC.getPrefixExcaped(g)));
				return;
			}
			unsetChan(args[1], g, tc);
			break;
		default:
			STATIC.errmsg(tc, help(STATIC.getPrefixExcaped(g)));
			break;
		}
		
	}

	
	/**
	 * hilfe: gibt Hilfe zu diesem Command als String zur�ck
	 */
	@Override
	public String help(String prefix) {
		return "Set or unset or list channels which are duplicated when somebody joins\n(see Permission *autochannel* in Command perm get)\n"
				+"*Syntax*: "+prefix+"autochannel set/add <ID of the AutoChannel>, remove/unset/rem/delete/del <ID of the AutoChannel>, list";
	}
	@Override
	public String getCommandType() {
		return CMD_TYPE_BOT_MODERATION;
	}
}
