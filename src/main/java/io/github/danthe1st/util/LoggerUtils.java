package io.github.danthe1st.util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * utilities for Logging
 */
public class LoggerUtils extends ListenerAdapter {	 
	private final static String logfileName = "cmdlog.txt";
	/**
	 * gets current time in format [dd.MM.yyyy - HH:mm:ss]
	 * @return current time in format [dd.MM.yyyy - HH:mm:ss]
	 */
	private static String getCurrentSystemTime() {
        return getCurrentSystemTime("[dd.MM.yyyy - HH:mm:ss]");
    }
	/**
	 * gets current time in a specified format
	 * @param format the format
	 * @return the time in the Format
	 */
	private static String getCurrentSystemTime(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date);
    }
	
	/**
	 * writes an executed Command in a Log file
	 * @param event The {@link MessageReceivedEvent} of the Command
	 */
    public static void logCommand(MessageReceivedEvent event) {
		File path=new File(STATIC.getSettingsDir() +"/"+event.getGuild().getId());
		if (!path.exists()) {
			path.mkdirs();
		}
		String s=String.format( "%s '%s' executed by %s in Channel %s (in Guild %s)", getCurrentSystemTime(), event.getMessage().getContentDisplay(), event.getMessage().getAuthor().getName(), event.getTextChannel().getName(),event.getGuild().getName());
		//server-local Log-File
		write(STATIC.getSettingsDir()+"/"+event.getGuild().getId()+"/" +logfileName, s);
		//global Log-File
		write(STATIC.getSettingsDir()+"/"+logfileName, s);
    }
    /**
     * writes text in a File<br>
     * every text-parameter will be terminated with a line break
     * @param filepath Der the path of the Log File
     * @param text The text to write
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