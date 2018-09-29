package commands.utils.spam;


import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import util.STATIC;

public class MsgSpammer implements Runnable{
	private static MsgSpammer spammer=new MsgSpammer();
	
	private Map<Guild, SpamWrapper> spams;
	private Thread spammerThread;
	
	private MsgSpammer() {
		spams=new HashMap<Guild, MsgSpammer.SpamWrapper>();
		spammerThread=null;
	}
	
	@Override
	public void run() {
		while (!spams.isEmpty()) {
			Set<Guild> toRemove=new HashSet<>();
			spams.forEach((k,v)->{
				STATIC.msg(v.channel, v.msg, Color.black, false);
				v.count--;
				if (v.count<=0) {
					toRemove.add(k);
				}
			});
			for (Guild rem : toRemove) {
				spams.remove(rem);
			}
		}
		spammerThread=null;
	}

	public static void addMsgSpam(int count,TextChannel channel,String msg) {
		SpamWrapper wrapper=new SpamWrapper(count, channel, msg);
		spammer.spams.put(channel.getGuild(), wrapper);
		
		if (spammer.spammerThread==null) {
			spammer.spammerThread=new Thread(spammer);
			spammer.spammerThread.start();
		}
	}
	
	private static class SpamWrapper{
		private int count;
		private TextChannel channel;
		private String msg;
		public SpamWrapper(int count, TextChannel channel, String msg) {
			this.count = count;
			this.channel = channel;
			this.msg = msg;
		}
		
	}
}
