package commands.admin;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.ListWrapper;
import util.STATIC;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import commands.Command;
import commands.CommandType;
import core.BotCommand;
import core.PermsCore;
/**
 * Command to Evaluate Code
 * @author Daniel Schmid
 */
@BotCommand(aliases = "blacklist")
public class CmdBlacklist implements Command{
	private static List<String> blacklist=new ArrayList<>();
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.checkOwner(event);	
	}
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
        if (args.length==0) {
			StringBuilder sb=new StringBuilder("Blacklisted Users:\n");
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
        STATIC.msg(event.getTextChannel(), "added "+toAdd.size()+" and removed "+toRemove.size()+" Users from the Blacklist\n"
        		+ "users **added**:\n"
        		+ addbuilder.toString()
        		+ "users **removed:\n**"
        		+ rembuilder.toString());
        saveBlacklist();
	}

	@Override
	public String help(String prefix) {
		return "Command to prevent users from using the bot **globally**\n"
				+ "**CAN ONLY BE USED BY *the bot-admin***";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.ADMIN;
	}
	
	
	public static boolean isBlacklisted(String userId) {
		return blacklist.contains(userId);
	}
	
	
	
	public static void loadBlacklist(JDA jda) {
		try {
			final File file=new File(STATIC.getSettingsDir()+"/blacklist.xml");
			JAXBContext context=JAXBContext.newInstance(ListWrapper.class);
			Unmarshaller um = context.createUnmarshaller();

		        // Reading XML from the file and unmarshalling.
			@SuppressWarnings("unchecked")
			ListWrapper<String> data = (ListWrapper<String>) um.unmarshal(file);
			blacklist=data.getData();
		} catch (JAXBException e) {
		}
	}
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
