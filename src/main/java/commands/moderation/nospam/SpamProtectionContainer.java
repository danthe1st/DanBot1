package commands.moderation.nospam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import io.github.danthe1st.util.MapWrapper;
import io.github.danthe1st.util.STATIC;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.PermissionException;
/**
 * Wrapper-Class for spam-protection
 * @author Daniel Schmid
 */
@XmlRootElement
public class SpamProtectionContainer {
	
	private static Map<String, SpamProtectionContainer> protectors=new HashMap<>();
	
	private SpamProtectType type;
	private int time;
	private int tries;
	private Map<String, UserStorage> userSpamMap=new HashMap<>();
	private SortedMap<Long, String[]> timeSpamMap=new TreeMap<>();
	private Timer timer;
	/**
	 * no-args-Constructor for JAXB
	 */
	public SpamProtectionContainer() {}
	private SpamProtectionContainer(SpamProtectType type,int tries, int time) {
		this.type=type;
		this.time=time;
		this.tries=tries;
	}
	public SpamProtectType getType() {
		return type;
	}
	public void setType(SpamProtectType type) {
		this.type = type;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public int getTries() {
		return tries;
	}
	public void setTries(int tries) {
		this.tries = tries;
	}
	public static void addSpamContainer(Guild g,SpamProtectType type,int tries, int time) {
		protectors.put(g.getId(), new SpamProtectionContainer(type, tries, time));
		save();
	}
	public static void removeSpamContainer(Guild g) {
		protectors.remove(g.getId());
		save();
	}
	public static SpamProtectionContainer getSpamContainer(Guild g) {
		return protectors.get(g.getId());
	}
	
	public static boolean isGuildProtected(Guild g) {
		return protectors.containsKey(g.getId());
	}
	
	public static boolean addMessage(Message message) {
		if (protectors.containsKey(message.getGuild().getId())) {
			return protectors.get(message.getGuild().getId()).addMsg(message);
		}
		return true;
	}
	/**
	 * registeres a Message to the spam-protection
	 * @param msg the Message
	 * @return <code>true</code> if the Message does not violate the Spam-restrictions
	 */
	private boolean addMsg(Message msg) {
		if (msg.getChannel().getName().toLowerCase().contains("spam")) {
			return true;
		}
		synchronized (userSpamMap) {
			UserStorage storage=new UserStorage(tries);
			if (userSpamMap.containsKey(msg.getAuthor().getId())) {
				storage=userSpamMap.get(msg.getAuthor().getId());
				if (storage.tries>0) {
					storage.tries--;
				}
				else {
					doAction(msg);
				}
				return true;
			}
			long t=System.currentTimeMillis()+time;
			userSpamMap.put(msg.getAuthor().getId(), storage);
			String[] spams=null;
			if (timeSpamMap.containsKey(t)) {
				spams=new String[timeSpamMap.get(t).length];
				for (int i = 0; i < timeSpamMap.get(t).length; i++) {
					spams[i+1]=timeSpamMap.get(t)[i];
				}
			}
			else {
				spams=new String[1];
			}
			spams[0]=msg.getAuthor().getId();
			timeSpamMap.put(System.currentTimeMillis()+time, spams);
			reloadTimer();
			return false;
		}
	}
	/**
	 * performs the administrative Action of that spam
	 * @param msg the (spammed) Message
	 */
	private void doAction(Message msg) {
		if (msg.getAuthor().getId().equals(msg.getJDA().getSelfUser().getId())) {
			return;
		}
		System.out.println("\""+msg.getAuthor().getName()+"\" spammed the message \""+msg.getContentDisplay()+"\" in Guild \""+msg.getGuild().getName()+"\", Action: "+type.getName());
		try {
			switch (type) {
			case ban:
				msg.delete().queue();
				msg.getGuild().getController().ban(msg.getAuthor().getId(),1, "autoban by "+msg.getGuild().getMember(msg.getGuild().getJDA().getSelfUser()).getEffectiveName()+" \nReason: spamming").queue();
				break;
			case kick:
				msg.delete().queue();
				msg.getGuild().getController().kick(msg.getAuthor().getId(),"autokick by "+msg.getGuild().getMember(msg.getGuild().getJDA().getSelfUser()).getEffectiveName()+" \nReason: spamming").queue();
				break;
			case delete:
				msg.delete().queue();
				break;
			default:
				break;
			}
		} catch (PermissionException e) {
			
		}
	}
	/**
	 * Wrapper-Class for a User
	 * @author Daniel Schmid
	 */
	private class UserStorage{
		private int tries;
		public UserStorage(int tries) {
			this.tries=tries;
		}
	}
	/**
	 * reloads the timer and performs actions for (spammed) messages
	 */
	private void reloadTimer() {
		List<Long> toRemove=new ArrayList<>();
		synchronized (timeSpamMap) {
			timeSpamMap.forEach((k,v)->{
				if (k.longValue()<System.currentTimeMillis()) {
					toRemove.add(k);
					for (String user : v) {
						userSpamMap.remove(user);
					}
				}
			});
			boolean changed=false;
			for (Long rem : toRemove) {
				for(String msgAction:timeSpamMap.remove(rem)) {
					synchronized (userSpamMap) {
						userSpamMap.remove(msgAction);
					}
					
				}
				if (!changed) {
					changed=true;
				}
			}
			
			if (timeSpamMap.isEmpty()) {
				return;
			}
			long time=timeSpamMap.firstKey();
			String[] potentialSpams=timeSpamMap.get(time);
			if (timer!=null) {
				timer.cancel();
				timer=null;
			}
			timer=new Timer();
			long delay=time-System.currentTimeMillis();
			if (delay<0) {
				for (String user : potentialSpams) {
					synchronized (userSpamMap) {
						userSpamMap.remove(user);
					}
					
				}
			}
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					for (String user : potentialSpams) {
						synchronized (userSpamMap) {
							userSpamMap.remove(user);
						}
						
					}
					timeSpamMap.remove(time);					
				}
			},delay);
		}
	}
	/**
	 * saves the settings
	 */
	public static void save() {
		Map<String, SpamProtectType> data=new HashMap<>();
		protectors.forEach((k,v)->{
			data.put(k, v.type);
		});
		File file=new File(STATIC.getSettingsDir()+"/spamProtections.xml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.err.println("cannot create File spamProtections.xml");
				return;
			}
		}
		try {
			JAXBContext context = JAXBContext
			        .newInstance(MapWrapper.class,SpamProtectionContainer.class);
			Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	        m.marshal(new MapWrapper<>(protectors), file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	/**
	 * loads the settings
	 */
	@SuppressWarnings("unchecked")
	public static void load() {
		try {
			final File file=new File(STATIC.getSettingsDir()+"/spamProtections.xml");
			if (!file.exists()) {
				return;
			}
			JAXBContext context=JAXBContext.newInstance(MapWrapper.class,SpamProtectionContainer.class);
			Unmarshaller um = context.createUnmarshaller();

		        // Reading XML from the file and unmarshalling.
			
			protectors = ((MapWrapper<String, SpamProtectionContainer>) um.unmarshal(file)).getData();
			
		} catch (JAXBException e) {
		}
	}
}
