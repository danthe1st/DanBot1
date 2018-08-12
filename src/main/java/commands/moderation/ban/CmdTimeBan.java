package commands.moderation.ban;

import net.dv8tion.jda.core.entities.Member;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import commands.Command;
import core.PermsCore;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command to ban a {@link Member}
 * @author Daniel Schmid
 *
 */
public class CmdTimeBan implements Command{//TODO
	//private String[] toTry=new String[] {"DD:HH:mm"};
	private long getBanTime(String time) {
		int d=0;
		int h=0;
		int min=0;
		
		//Minuten
		int posEnde=time.indexOf(":");
		if (posEnde==-1) {
			posEnde=time.length();
		}
		int posAnfang=0;
		//suche Minutenstring
		String sH="";
		try {
			sH=time.substring(posAnfang,posEnde);
		} catch (StringIndexOutOfBoundsException e) {
			
		}
		catch (Exception e) {
			
		}
		//parse Minutenstring
		
		try {
			d=Integer.parseInt(sH);
		} catch (NumberFormatException e) {
		}
		catch (Exception e) {
		}
		
		if (posEnde<time.length()) {
			//Sekunden
			posAnfang=posEnde+1;
			String substr=time.substring(posAnfang);
			int colonIndex=substr.indexOf(":");
			if (colonIndex==-1) {
				posEnde=posAnfang+substr.length();
			}
			else {
				posEnde+=colonIndex+1;
			}
			
			
			//suche Sekundenstring
			String sSek="";
			try {
				sSek=time.substring(posAnfang,posEnde);
			} catch (StringIndexOutOfBoundsException e) {
			}
			catch (Exception e) {
			}
			//parse Sekundenstring
			
			try {
				h=Integer.parseInt(sSek);
			} catch (NumberFormatException e) {
			}
			catch (Exception e) {
			}
			
			//Zehntelsekunden
			if (posEnde<time.length()) {
				posAnfang=posEnde+1;
				posEnde=time.length();
				//suche Zehntelsekundenstring
				String sZSek="";
				try {
					sZSek=time.substring(posAnfang,posEnde);
				} catch (StringIndexOutOfBoundsException e) {
				}
				catch (Exception e) {
				}
				
				
				//parse Zehntelsekundenstring
				try {
					min=Integer.parseInt(sZSek);
				} catch (NumberFormatException e) {
				}
				catch (Exception e) {
				}
			}
		}
		//									ms	 s	 min	 h
		return   System.currentTimeMillis()+1000*60*(min+60+(h+24*d));
		//return ((d*60+h)*60+min)*1000+System.currentTimeMillis();
		
//		for (String string : toTry) {
//			DateFormat formatter = new SimpleDateFormat(string);
//			try {
//				return formatter.parse(time).getTime()+System.currentTimeMillis();
//			} catch (ParseException e) {
//			}
//		}
//		return -1;
	}
	

	/**
	 * Der Befehl selbst(siehe help)
	 */
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(!PermsCore.check(event, "ban")) {
			return;
		}		
		if (args.length<2) {
			STATIC.errmsg(event.getTextChannel(), "missing args!");
			return;
		}
		long time=getBanTime(args[0]);
		if (time==-1) {
			STATIC.errmsg(event.getTextChannel(), "invalid time definition");
		}
		
		List<Member> users= event.getGuild().getMembersByName(args[1], true);
		String reason=null;
		int argCount=2;
		if (args.length>argCount) {
			reason="";
			for (int i = argCount; i < args.length; i++) {
				reason=reason+args[i];
				
			}
		}
		if (reason==null||reason.equals("")) {
			DateFormat format=new SimpleDateFormat("dd.MM.YYYY,HH:mm:ss");
			
			reason="timeban from user "+event.getAuthor().getName()+", expires: "+format.format(time);
		}
		for (Member user : users) {
			
			try {
				event.getGuild().getController().ban(user,0, reason).queue();
				AutoUnbanner.addUnBan(event.getGuild(), user.getUser(), time);
			} catch (Exception e) {
				STATIC.errmsg(event.getTextChannel(), "unknown Error banning user "+user.getNickname());
			}
		}
	}
	public String help(String prefix) {
		return "bans a user (see Permission *ban* in Command perm get)\n"
				+"*Syntax*: "+prefix+"tban <time> <victim> (<reason>)";
	}
	@Override
	public String getCommandType() {
		return CMD_TYPE_GUILD_MODERATION;
	}
}
