package io.github.danthe1st.danbot1.commands.admin;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.ListWrapper;
import io.github.danthe1st.danbot1.util.STATIC;
/**
 * Command to Evaluate Code
 * @author Daniel Schmid
 */
@BotCommand("blacklist")
public class CmdBlacklist implements Command{
	private static List<String> blacklist=new ArrayList<>();
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.checkOwner(event);	
	}
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		Guild g=event.getGuild();
        if (args.length==0) {
			StringBuilder sb=new StringBuilder(translate(g, "UsersBlacklisted")+":\n");
			for (String string : blacklist) {
				if (event.getJDA().getUserById(string)==null) {
					sb.append(string);
				}
				else {
					sb.append(event.getJDA().getUserById(string));
				}
				sb.append("\n");
			}
			STATIC.msg(event.getTextChannel(), sb.toString());
			return;
		}
        List<User> toAdd=new ArrayList<>();
        List<User> toRemove=new ArrayList<>();
        for (String string : args) {
        	try {
        		User user=event.getJDA().getUserById(string);
        		if (user!=null) {
        			if (isBlacklisted(string)) {
						toRemove.add(user);
					}
					else {
						toAdd.add(user);
					}
				}
			} catch (NumberFormatException e) {
				//ignore
			}
			
		}
        StringBuilder addbuilder=new StringBuilder();
        StringBuilder rembuilder=new StringBuilder();
        for (User user : toAdd) {
			blacklist.add(user.getId());
			addbuilder.append(user+"\n");
		}
        for (User user : toRemove) {
			blacklist.remove(user.getId());
			rembuilder.append(user+"\n");
		}
        STATIC.msg(event.getTextChannel(), 
        		String.format(translate(g,"addedRemovedUsersFromBlacklist"),
        				toAdd.size(),toRemove.size(),addbuilder.toString(),rembuilder.toString()));
        saveBlacklist();
	}

	@Override
	public String help() {
		return "blacklistHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.ADMIN;
	}
	/**
	 * checks if a user is Blacklisted
	 * @param userId the ISnowflake ID of the user
	 * @return <code>true</code> if the user is blacklisted, <code>false</code> if not
	 */
	public static boolean isBlacklisted(String userId) {
		return blacklist.contains(userId);
	}
	
	
	/**
	 * loads the blacklist data
	 */
	public static void loadBlacklist() {
		try {
			final File file=new File(STATIC.getSettingsDir()+"/blacklist.xml");
			JAXBContext context=JAXBContext.newInstance(ListWrapper.class);
			Unmarshaller um = context.createUnmarshaller();

		        // Reading XML from the file and unmarshalling.
			@SuppressWarnings("unchecked")
			ListWrapper<String> data = (ListWrapper<String>) um.unmarshal(file);
			blacklist=data.getData();
		} catch (JAXBException e) {
			//ignore
		}
	}
	/**
	 * saves the blacklist data
	 */
	private static void saveBlacklist() {
		File file=new File(STATIC.getSettingsDir()+"/blacklist.xml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("cannot create File blacklist.xml");
				return;
			}
		}
		try {
			JAXBContext context = JAXBContext
			        .newInstance(ListWrapper.class);
			Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	        m.marshal(new ListWrapper<String>(blacklist), file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}
