package io.github.danthe1st.danbot1.listeners;

import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
/**
 * automatically sends an Invite to the admin user when the Bot joines a {@link Guild}
 * If the user gets banned it will be unbanned and invited
 * @author Daniel Schmid
 */
@BotListener
public class GuildChangeListener extends ListenerAdapter {
	/**
	 * listener when the Bot joines the {@link Guild}<br>
	 * Sends a message(with an Invite) to the Bot Owner
	 */
	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		File dir=new File(STATIC.getSettingsDir()+"/"+event.getGuild().getId());
		if (dir.exists()) {
			File leftFile=new File(dir,"/.left");
			if (leftFile.exists()) {
				if(!leftFile.delete()) {
					System.err.println("cannot delete directory: "+dir.getAbsolutePath());
				}
			}
		}
		else {
			if(!dir.mkdir()) {
				System.err.println("cannot create directory: "+dir.getAbsolutePath());
			}
		}
		
		File dataFile=new File(dir,"guildinfo.xml");
		
		saveGuildData(event.getGuild(),dataFile);
	}
	/**
	 * listener when a user leaves a {@link Guild}<br>
	 * If the bot itself leaves the guild, the Bot Owner gets notified
	 */
	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		File dir=new File(STATIC.getSettingsDir()+"/"+event.getGuild().getId());
		if (dir.exists()) {
			try {
				new File(dir, ".left").createNewFile();
			} catch (IOException e) {
			}
		}
	}
	/**
	 * listener when the name of the {@link Guild} changes
	 */
	@Override
	public void onGuildUpdateName(GuildUpdateNameEvent event) {
		saveGuildData(event.getGuild());
	}
	/**
	 * saves basic information of a Guild to a FIle
	 * @param g the {@link Guild}
	 */
	public static void saveGuildData(Guild g) {
		File dir=new File(STATIC.getSettingsDir()+"/"+g.getId());
		if (!dir.exists()) {
			if(!dir.mkdir()) {
				System.err.println("cannot create directory: "+dir.getAbsolutePath());
			}
		}
		saveGuildData(g, new File(dir,"guildinfo.xml"));
	}
	/**
	 * saves basic information of a Guild to a FIle
	 * @param g the {@link Guild}
	 * @param dataFile the File where the Information should be saved
	 */
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
	/**
	 * Wrapper-Class for Guild Data
	 * @author Daniel Schmid
	 */
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
