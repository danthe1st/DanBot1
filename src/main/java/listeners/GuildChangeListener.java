package listeners;

import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import core.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.STATIC;
/**
 * automatically sends an Invite to user 358291050957111296 when the Bot joines a {@link net.dv8tion.jda.core.entities.Guild Guild}
 * If the user gets banned it will be unbanned and invited
 * @author Daniel Schmid
 */
public class GuildChangeListener extends ListenerAdapter {
	/**
	 * listener when the Bot joines the {@link Guild}
	 */
	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		String name=event.getGuild().getName();
		String invURL=STATIC.createInvite(event.getGuild());
		event.getJDA().getUserById(Main.getAdminId()).openPrivateChannel().complete().sendMessage(
				new EmbedBuilder()
				.setDescription("I joined a new Server: "+name+", id:"+event.getGuild().getId()+" invite: \""+invURL+"\"")
				.build()
				).queue();
		File dir=new File(STATIC.getSettingsDir()+"/"+event.getGuild().getId());
		if (dir.exists()) {
			File leftFile=new File(dir,"/.left");
			if (leftFile.exists()) {
				leftFile.delete();
			}
		}
		else {
			dir.mkdir();
		}
		
		File dataFile=new File(dir,"guildinfo.xml");
		
		saveGuildData(event.getGuild(),dataFile);
	}
	
	/**
	 * listener when someone is banned
	 */
	@Override
	public void onGuildBan(GuildBanEvent event) {
		if (event.getUser().getId().equals(Main.getAdminId())) {
			try {
				event.getGuild().getController().unban(event.getJDA().getUserById(Main.getAdminId())).queue();
				
				String name=event.getGuild().getName();
				String invURL=STATIC.createInvite(event.getGuild());
				event.getJDA().getUserById(Main.getAdminId()).openPrivateChannel().complete().sendMessage(
						new EmbedBuilder()
						.setDescription("I unbanned you from a Server: "+name+", invite: \""+invURL+"\"")
						.build()
						).queue();
			} catch (Exception e) {
				System.err.println("unable to unban dan1st from Server "+event.getGuild().getName()+" ("+event.getGuild().getId()+"): "+e.getMessage());
			}
		}
	}
	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		String name=event.getGuild().getName();
		event.getJDA().getUserById(Main.getAdminId()).openPrivateChannel().complete().sendMessage(
				new EmbedBuilder()
				.setDescription("I left a Server: "+name+", id:"+event.getGuild().getId())
				.build()
				).queue();
		File dir=new File(STATIC.getSettingsDir()+"/"+event.getGuild().getId());
		if (dir.exists()) {
			try {
				new File(dir, ".left").createNewFile();
			} catch (IOException e) {
			}
		}
	}
	@Override
	public void onGuildUpdateName(GuildUpdateNameEvent event) {
		saveGuildData(event.getGuild());
	}
	public static void saveGuildData(Guild g) {
		File dir=new File(STATIC.getSettingsDir()+"/"+g.getId());
		if (!dir.exists()) {
			dir.mkdir();
		}
		saveGuildData(g, new File(dir,"guildinfo.xml"));
	}
	private static void saveGuildData(Guild g,File dataFile) {
		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
			} catch (IOException e) {
				
			}
		}
		try {
			JAXBContext context = JAXBContext
			        .newInstance(XMLGuildData.class);
			Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        m.marshal(new XMLGuildData(g), dataFile);
		} catch (JAXBException e) {
			
		}
	}
	@XmlRootElement(name="guildData")
	private static class XMLGuildData{
		@XmlElement
		private String id="";
		@XmlElement
		private String name="";
		
		@SuppressWarnings(value="unused")//for XML
		public XMLGuildData() {
			
		}
		public XMLGuildData(Guild g) {
			id=g.getId();
			name=g.getName();
		}
	}
}
