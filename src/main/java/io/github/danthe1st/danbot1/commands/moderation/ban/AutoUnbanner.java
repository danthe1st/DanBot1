package io.github.danthe1st.danbot1.commands.moderation.ban;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

import io.github.danthe1st.danbot1.util.MapWrapper;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * Class for automatically unbanning a user (timeban)
 * @author Daniel Schmid
 */
public class AutoUnbanner {

	private static AutoUnbanner unbanner;
	private SortedMap<Long, Unban> unbans=new TreeMap<>();
	private Timer timer;
	private JDA jda;
	private AutoUnbanner(JDA jda) {
		this.jda=jda;
	}
	/**
	 * unbans all users that have not been unbanned and set a timer for unbanning
	 */
	private void reloadTimer() {
		List<Long> toRemove=new ArrayList<>();
		synchronized (unbans) {
			unbans.forEach((k,v)->{
				if (k.longValue()<System.currentTimeMillis()) {
					for (String user : v.getUsers()) {
						unban(v.guild(jda), user);
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
				for (String user : unban.getUsers()) {
					unban(unban.guild(jda), user);
				}
			}
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					for (String user : unban.getUsers()) {
						unban(unban.guild(jda), user);
					}
					unbans.remove(time);
					saveUnBans();
				}
			},delay);
		}
	}
	/**
	 * unbans an user
	 * @param g the {@link Guild} where th user should be unbanned
	 * @param user the ISnowflake ID of the user to be unbanned
	 */
	private void unban(Guild g,String user) {
		g.unban(user).queue();
		System.out.println("unbanned user with id ["+user+"] from guild "+g.getName());
		User jdaUser=jda.getUserById(user);
		String inv=STATIC.createInvite(g);
		if (jdaUser!=null) {
			String msg=String.format(translate(g,"tbanEnd"),g.getName());
			if (inv!=null) {
				msg+=translate(g,"invite")+inv;
			}
			jdaUser.openPrivateChannel().complete().sendMessage(msg).queue();
		}
	}
	/**
	 * registeres an user for unbanning
	 * @param g the {@link Guild} where th user should be unbanned
	 * @param user the ISnowflake ID of the user to be unbanned
	 * @param systime the time when the user should be unbanned
	 */
	public static void addUnBan(Guild g,User user,long systime) {
		
		Unban unban=new Unban(g, user);
		boolean needreload;
		getInstance(user.getJDA());
		synchronized (unbanner.unbans) {
			getInstance(g.getJDA()).unbans.put(Long.valueOf(systime), unban);
			needreload=unbanner.unbans.get(unbanner.unbans.firstKey())==unban;
		}
		if (!unbanner.unbans.isEmpty()&& needreload) {
			unbanner.reloadTimer();
		}
		saveUnBans();
	}
	/**
	 * Factory-Method
	 * @param jda The JDA Object
	 * @return the {@link AutoUnbanner}
	 */
	private static synchronized AutoUnbanner getInstance(JDA jda) {
		
		if (unbanner==null) {
			unbanner=new AutoUnbanner(jda);
		}
		if (!unbanner.unbans.isEmpty()) {
			unbanner.reloadTimer();
		}
		return unbanner;
	}
	/**
	 * loads the unbans from a file
	 * @param jda the JDA Object
	 */
	public static void loadUnBans(JDA jda) {
		try {
			final File file=new File(STATIC.getSettingsDir()+"/unbans.xml");
			JAXBContext context=JAXBContext.newInstance(MapWrapper.class,Unban.class);
			Unmarshaller um = context.createUnmarshaller();

		        // Reading XML from the file and unmarshalling.
			@SuppressWarnings("unchecked")
			MapWrapper<Long,Unban> data = (MapWrapper<Long,Unban>) um.unmarshal(file);
			getInstance(jda).unbans=new TreeMap<>(data.getData());
			if (!unbanner.unbans.isEmpty()) {
				unbanner.reloadTimer();
			}
		} catch (JAXBException e) {
			//ignore
		}
	}
	/**
	 * saves the unbans to a file
	 */
	public static void saveUnBans() {
		File file=new File(STATIC.getSettingsDir()+"/unbans.xml");
		if (!file.exists()) {
			try {
				Files.createFile(file.toPath());
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
