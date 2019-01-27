package io.github.danthe1st.commands.utils.spam;


import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.danthe1st.util.STATIC;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
/**
 * Class for the Spammer Thread
 * @author Daniel Schmid
 */
public class MsgSpammer implements Runnable{
	private static MsgSpammer spammer=new MsgSpammer();
	
	private Map<Guild, SpamWrapper> spams;
	private Thread spammerThread;
	
	private MsgSpammer() {
		spams=new HashMap<Guild, MsgSpammer.SpamWrapper>();
		spammerThread=null;
	}
	
	@Override
	public synchronized void run() {
		while (!spams.isEmpty()) {
			try {
				Set<Guild> toRemove=new HashSet<>();
				spams.forEach((k,v)->{
					
					STATIC.msg(v.channel, new EmbedBuilder()
							.setDescription(v.msg)
							.setFooter("spam-command -> "+v.commander.getName(), v.commander.getAvatarUrl())
							.setColor(Color.BLACK)
							.build() , false);
					v.count--;
					if (v.count<=0) {
						toRemove.add(k);
					}
				});
				for (Guild rem : toRemove) {
					spams.remove(rem);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		spammerThread=null;
	}
	/**
	 * adds messages to be spammed
	 * @param count the number of messages to be spammed
	 * @param channel where to spam
	 * @param msg the Message that should be spammed
	 * @param commander the user that commanded the spam
	 */
	public synchronized static void addMsgSpam(int count,TextChannel channel,String msg,User commander) {
		if (count==0) {
			spammer.spams.remove(channel.getGuild());
			return;
		}
		SpamWrapper wrapper=new SpamWrapper(count, channel, msg,commander);
		spammer.spams.put(channel.getGuild(), wrapper);
		
		if (spammer.spammerThread==null) {
			spammer.spammerThread=new Thread(spammer,"spam-Thread");
			spammer.spammerThread.start();
		}
	}
	/**
	 * Wrapper-Class for spam-commands
	 * @author Daniel Schmid
	 */
	private static class SpamWrapper{
		private int count;
		private TextChannel channel;
		private String msg;
		private User commander;
		public SpamWrapper(int count, TextChannel channel, String msg,User commander) {
			this.count = count;
			this.channel = channel;
			this.msg = msg;
			this.commander=commander;
		}
		
	}
}
