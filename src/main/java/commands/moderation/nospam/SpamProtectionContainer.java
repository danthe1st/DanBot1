package commands.moderation.nospam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlRootElement;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.exceptions.PermissionException;

@XmlRootElement
public class SpamProtectionContainer {//TODO timing(Threads)

	private static Map<String, SpamProtectionContainer> protectors=new HashMap<>();
	
	private SpamProtectType type;
	private int time;
	private int tries;
	private Map<String, UserStorage> userSpamMap=new HashMap<>();
	private SortedMap<Long, String[]> timeSpamMap=new TreeMap<>();
	private Timer timer;
	
	private SpamProtectionContainer(SpamProtectType type,int tries, int time) {
		this.type=type;
		this.time=time;
		this.tries=tries;
	}
	
	public SpamProtectType getType() {
		return type;
	}

	public int getTime() {
		return time;
	}

	public int getTries() {
		return tries;
	}

	public static void addSpamContainer(Guild g,SpamProtectType type,int tries, int time) {
		protectors.put(g.getId(), new SpamProtectionContainer(type, tries, time));
	}
	public static void removeSpamContainer(Guild g) {
		protectors.remove(g.getId());
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
	private boolean addMsg(Message msg) {
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
	private void doAction(Message msg) {
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
	private class UserStorage{
		private int tries;
		public UserStorage(int tries) {
			this.tries=tries;
		}
		
	}
	
	
	
	
	
	
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
			if (changed) {
				//TODO save
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
					//TODO save
					
				}
			},delay);
		}
	}
}
