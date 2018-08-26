package commands.moderation.ban;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import util.MapWrapper;
import util.STATIC;

public class AutoUnbanner {

	private static AutoUnbanner unbanner;
	private SortedMap<Long, Unban> unbans=new TreeMap<>();
	private Timer timer;
	private JDA jda;
	private AutoUnbanner(JDA jda) {
		this.jda=jda;
	}
	private void reloadTimer() {
		List<Long> toRemove=new ArrayList<>();
		synchronized (unbans) {
			unbans.forEach((k,v)->{
				if (k.longValue()<System.currentTimeMillis()) {
					for (String user : v.user()) {
						v.guild(jda).getController().unban(user).queue();
						
						toRemove.add(k);
						
					}
				}
			});
			boolean changed=false;
			for (Long rem : toRemove) {
				unbans.remove(rem);
				if (!changed) {
					changed=true;
				}
			}
			if (changed) {
				saveUnBans();
			}
			if (unbans.isEmpty()) {
				return;
			}
			long time=unbans.firstKey();
			Unban unban=unbans.get(time);
			if (timer!=null) {
				timer.cancel();
				timer=null;
			}
			timer=new Timer();
			long delay=time-System.currentTimeMillis();
			if (delay<0) {
				for (String user : unban.user()) {
					unban.guild(jda).getController().unban(user).queue();
					User JDAUser=jda.getUserById(user);
					String inv=STATIC.createInvite(jda.getGuildById(unban.getGuild()));
					if (JDAUser!=null) {
						String msg="Your timeban from the Server "+unban.getGuild()+" ran out";
						if (inv!=null) {
							msg+="Invite: "+inv;
						}
						JDAUser.openPrivateChannel().complete().sendMessage(msg).queue();
					}
					
				}
			}
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					for (String user : unban.user()) {
						unban.guild(jda).getController().unban(user).queue();
					}
					unbans.remove(time);
					saveUnBans();
					
				}
			},delay);
		}
	}
	public static void addUnBan(Guild g,User user,long systime) {
		
		Unban unban=new Unban(g, user);
		boolean needreload;
		synchronized (unbanner.unbans) {
			getUnbanner(g.getJDA()).unbans.put(Long.valueOf(systime), unban);
			needreload=unbanner.unbans.get(unbanner.unbans.firstKey())==unban;
		}
		if (!unbanner.unbans.isEmpty()&& needreload) {
			unbanner.reloadTimer();
		}
		saveUnBans();
	}
	private static AutoUnbanner getUnbanner(JDA jda) {
		
		if (unbanner==null) {
			unbanner=new AutoUnbanner(jda);
		}
		if (!unbanner.unbans.isEmpty()) {
			unbanner.reloadTimer();
		}
		return unbanner;
	}
	public static void loadUnBans(JDA jda) {
		try {
			final File file=new File(STATIC.getSettingsDir()+"/unbans.xml");
			JAXBContext context=JAXBContext.newInstance(MapWrapper.class,Unban.class);
			 Unmarshaller um = context.createUnmarshaller();

		        // Reading XML from the file and unmarshalling.
			 @SuppressWarnings("unchecked")
			 MapWrapper<Long,Unban> data = (MapWrapper<Long,Unban>) um.unmarshal(file);
			 getUnbanner(jda).unbans=new TreeMap<>(data.getData());
			 if (!unbanner.unbans.isEmpty()) {
				 unbanner.reloadTimer();
			 }
		} catch (JAXBException e) {
		}
	}
	public static void saveUnBans() {
		File file=new File(STATIC.getSettingsDir()+"/unbans.xml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("cannot create File unbans.xml");
				return;
			}
		}
		try {
			JAXBContext context = JAXBContext
			        .newInstance(MapWrapper.class,Unban.class);
			Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	        m.marshal(new MapWrapper<Long,Unban>(unbanner.unbans), file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
}
