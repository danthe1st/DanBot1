package util;
/*
	BEARBEITET!!!
		von Daniel Schmid

	Created by:	  Schlaubi (github.com/DRSchlaubi)
	Contributor:  zekro (github.com/zekrotja)
	
	READ BEFORE USAGE: http://s.zekro.de/codepolicy
	Following #07, this code is also fully covered 
	by this licence with same conditions!

*/


import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utilities f. Log-Dateien usw.
 * @author Daniel Schmid
 *
 */
public class LoggerUtils extends ListenerAdapter {	 
	private final static String logfileName = "cmdlog.txt";

	/**
	 * gibt die Zeit als String im Format [dd.MM.yyyy - HH:mm:ss] zurück
	 * @return aktuelle Zeit als String
	 */
	private static String getCurrentSystemTime() {
        return getCurrentSystemTime("[dd.MM.yyyy - HH:mm:ss]");
    }
	private static String getCurrentSystemTime(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();

        return dateFormat.format(date);
    }
	
	/**
	 * schreibt einen ausgeführten Command in eine Log-Datei
	 * @param event Das MessageReceivedEvent des Commands
	 */
    public static void logCommand(MessageReceivedEvent event) {
		File path=new File(STATIC.getSettingsDir() +"/"+event.getGuild().getId());
		if (!path.exists()) {
			path.mkdirs();
		}
		String s=String.format( "%s '%s' executed by %s in Channel %s (in Guild %s)", getCurrentSystemTime(), event.getMessage().getContentDisplay(), event.getMessage().getAuthor().getName(), event.getTextChannel().getName(),event.getGuild().getName());
		//serverlokale Log-Datei
		write(STATIC.getSettingsDir()+"/"+event.getGuild().getId()+"/" +logfileName, s);
		//globale Log-Datei
		write(STATIC.getSettingsDir()+"/"+logfileName, s);
    }
    /**
     * schreibt Text in eine Datei<br>
     * Jeder übergegebene Parameter(text) wird mit einem Zeilenumbruch terminiert
     * @param filepath Der Pfad der Datei
     * @param text Der zu schreibende Text
     */
    private static void write(String filepath, String... text) {
    	if (text.length==0) {
			return;
		}
    	try {
			File file=new File(filepath);
			if (!file.exists()) {
				file.createNewFile();
			}
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))){
            	for (String string : text) {
                	bw.write(string);
                    bw.newLine();
    			}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}