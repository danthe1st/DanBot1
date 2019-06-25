package io.github.danthe1st.danbot1.commands.moderation.ban;

import net.dv8tion.jda.api.entities.Member;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Command to ban a {@link Member} until a specified time expires
 * @author Daniel Schmid
 */
@BotCommand({"tban","timeban"})
public class CmdTimeBan implements Command{
	/**
	 * parses/calculates the System time
	 * @param time the relative time as String
	 * @return the time in ms
	 */
	private long getBanTime(String time) {
		int d=0;
		int h=0;
		int min=0;
		
		//days
		int posEnde=time.indexOf(":");
		if (posEnde==-1) {
			posEnde=time.length();
		}
		int posAnfang=0;
		//get day-String
		String sH="";
		try {
			sH=time.substring(posAnfang,posEnde);
		} catch (StringIndexOutOfBoundsException e) {
			
		}
		catch (Exception e) {
			
		}
		//parse day-String
		
		try {
			d=Integer.parseInt(sH);
		} catch (NumberFormatException e) {
		}
		catch (Exception e) {
		}
		
		if (posEnde<time.length()) {
			//hours
			posAnfang=posEnde+1;
			String substr=time.substring(posAnfang);
			int colonIndex=substr.indexOf(":");
			if (colonIndex==-1) {
				posEnde=posAnfang+substr.length();
			}
			else {
				posEnde+=colonIndex+1;
			}
			
			
			//get h-String
			String sSec="";
			try {
				sSec=time.substring(posAnfang,posEnde);
			} catch (StringIndexOutOfBoundsException e) {
			}
			catch (Exception e) {
			}
			//parse h-String
			
			try {
				h=Integer.parseInt(sSec);
			} catch (NumberFormatException e) {
			}
			catch (Exception e) {
			}
			
			//minutes
			if (posEnde<time.length()) {
				posAnfang=posEnde+1;
				posEnde=time.length();
				//get min-String
				String sZSek="";
				try {
					sZSek=time.substring(posAnfang,posEnde);
				} catch (StringIndexOutOfBoundsException e) {
				}
				catch (Exception e) {
				}
				//parse min-String
				try {
					min=Integer.parseInt(sZSek);
				} catch (NumberFormatException e) {
				}
				catch (Exception e) {
				}
			}
		}
		//									ms	 s	 min	 h
		return   System.currentTimeMillis()+1000L*60*(min+60*(h+24*d));
	}
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.check(event, "ban");
	}
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(!PermsCore.check(event, "ban")) {
			return;
		}		
		if (args.length<2) {
			STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"missingArgs"));
			return;
		}
		long time=getBanTime(args[0]);
		if (time==-1) {
			STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"errArgNoTime"));
		}
		
		List<Member> users= event.getGuild().getMembersByName(args[1], true);
		String reason=null;
		int argCount=2;
		if (args.length>argCount) {
			StringBuilder reasonBuilder=new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				reasonBuilder.append(args[i]);
			}
			reason=reasonBuilder.toString();
		}
		if (reason==null||reason.equals("")) {
			DateFormat format=new SimpleDateFormat("dd.MM.YYYY,HH:mm:ss");
			
			reason=String.format(translate(event.getGuild(),"timebanReason"),event.getAuthor().getName(),format.format(time));
		}
		for (Member user : users) {
			
			try {
				event.getGuild().ban(user,0, reason).queue();
				AutoUnbanner.addUnBan(event.getGuild(), user.getUser(), time);
			} catch (Exception e) {
				STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"errCannotBan")+user.getEffectiveName());
			}
		}
	}
	public String help() {
		return "tbanHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.GUILD_MODERATION;
	}
}
