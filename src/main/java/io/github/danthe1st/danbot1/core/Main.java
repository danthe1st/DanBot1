package io.github.danthe1st.danbot1.core;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import java.util.Scanner;
import javax.security.auth.login.LoginException;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.console.Console;
import io.github.danthe1st.danbot1.listeners.BotListener;
import io.github.danthe1st.danbot1.util.BotSecurityManager;
import io.github.danthe1st.danbot1.util.LanguageController;
import io.github.danthe1st.danbot1.util.STATIC;
import io.github.danthe1st.danbot1.util.ScanCloser;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.JDAImpl;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
/**
 * <b>Main-Class</b><br>
 * Initiates the Bot
 * @author Daniel Schmid
 */
public class Main {
	private static String token=System.getProperty("token");
	private static final Scanner scan=new Scanner(System.in, StandardCharsets.UTF_8.name());
	private static String adminId="358291050957111296";
	private static String[] args;
	private static JDA jda=null;
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(new ScanCloser(scan)));
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				if (jda!=null) {
					jda.shutdown();
				}
			}
		}));
	}
	/**
	 * start-Method
	 * @param args 
	 * <pre> ?					show this help
	 * noevalsecurity				disables secure execution with e.g. the eval command
	 * game=&lt;name&gt;				Sets the activity name
	 * initgame=&lt;name&gt;			Sets the activity name before the Bot finished loading
	 * token=&lt;Discord Bot token&gt;		Sets the token of the Bot (you will be asked if it is not set)
	 * status=&lt;status&gt;			Sets a Discord-Status for the Bot
	 * initstatus=&lt;status&gt;			Sets a Discord-Status for the Bot before it finished loading
	 * 		do_not_disturb, idle, invisible, online, offline, unknown
	 * admin=&lt;id&gt;				sets the Bot admin to a specified user(you need the ID(ISnowfake ID) of the user)
	 * settings=&lt;directory&gt;			Sets the storage directory for the Bot
	 * </pre>
	 */
	public static void main(final String[] args) {
		OnlineStatus statusWhenLoaded=OnlineStatus.ONLINE;
		OnlineStatus statusBeforeLoaded=OnlineStatus.IDLE;
		String gameWhenLoaded=System.getProperty("game");
		System.setSecurityManager(new BotSecurityManager());
		if (gameWhenLoaded==null) {
			gameWhenLoaded="with you";
		}
		String gameBeforeLoaded=System.getProperty("initgame");
		if (gameBeforeLoaded==null) {
			gameBeforeLoaded="booting up - please wait";
		}
		Main.args=args.clone();
		for (String arg : args) {
			try {
				switch (arg.toLowerCase()) {
				case "?":
				case "help":
				case "-?":
				case "/help":
				case "-help":
					System.out.println("DanBot1 is a Discord Bot by Daniel Schmid\n"
							+ "See https://danthe1st.github.io/DanBot1/ for a list of all Commands\n"
							+ "possible arguments:\n"
							+ "\t?\tshow this help\n"
							+ "\tnoevalsecurity\tdisables secure execution with e.g. the eval command\n"
							+ "\tgame=<name>\tSets the activity name\n"
							+ "\tinitgame=<name>\tSets the activity name before the Bot finished loading\n"
							+ "\ttoken=<Discord Bot token> Sets the token of the Bot (you will be asked if it is not set)\n"
							+ "\tstatus=<status>\tSets a Discord-Status for the Bot\n"
							+ "\tinitstatus=<status>\tSets a Discord-Status for the Bot before it finished loading\n"
							+ 	"\t\tdo_not_disturb, idle, invisible, online, offline\n"
							+ "\tadmin=<id> sets the Bot admin to a specified user(you need the ID(ISnowfake ID) of the user)"
							+ "\tsettings=<directory>\tSets the storage directory for the Bot\n");
					return;
				case "noevalsecurity":
					System.setSecurityManager(null);
					break;
				default:
					if (arg.toLowerCase().startsWith("game=")) {
						gameWhenLoaded=getStringArgValue(arg, gameWhenLoaded).replaceAll("_", " ");
					}
					else if (arg.toLowerCase().startsWith("initgame=")) {
						gameBeforeLoaded=getStringArgValue(arg, gameBeforeLoaded).replaceAll("_", " ");
					}
					else if (arg.toLowerCase().startsWith("token=")) {
						token=getStringArgValue(arg, token);
					}
					else if (arg.toLowerCase().startsWith("settings=")) {
						STATIC.setSettingsDir(getStringArgValue(arg, null));
					}
					else if (arg.toLowerCase().startsWith("admin=")) {
						String newAdminId=getStringArgValue(arg, null);
						try {
							if (!(newAdminId==null||newAdminId.equals(""))) {
								Long.parseLong(adminId);
								adminId=newAdminId;
							}
						} catch (Exception e) {
						}
					}
					else if (arg.toLowerCase().startsWith("status=")) {
						statusWhenLoaded=loadStatus(arg.substring(7), statusWhenLoaded);
					}
					else if (arg.toLowerCase().startsWith("initstatus=")) {
						statusBeforeLoaded=loadStatus(arg.substring(11), statusBeforeLoaded);
					}
					
				}
			} catch (IndexOutOfBoundsException e) {
			}
		}
		System.out.println("DanBot1 by Daniel Schmid");
		boolean alreadyDone=false;
		while (true) {
			while (alreadyDone||token==null||token.equals("")) {
				System.out.println("Please enter a valid Bot token:");
				token=scan.next();
				alreadyDone=false;
			}
			alreadyDone=true;
			
			final JDABuilder builder=new JDABuilder(AccountType.BOT);
			builder.setToken(token);	
			builder.setAutoReconnect(true);
			builder.setStatus(statusBeforeLoaded);
			builder.setActivity(Activity.playing(gameBeforeLoaded));
			
			builder.setRequestTimeoutRetry(true);
			
			ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
			
		    configurationBuilder.addUrls(ClasspathHelper.forJavaClassPath());
		    loadPlugins(configurationBuilder);
		   
		    Reflections ref = new Reflections(configurationBuilder);
		    addCommandsAndListeners(ref,builder);
			try {
				jda=builder.build();
				Console.runConsole(scan, jda);
				jda.awaitReady();
				((JDAImpl) jda).getGuildSetupController().clearCache();
				jda.getPresence().setStatus(statusWhenLoaded);
				jda.getPresence().setActivity(Activity.playing(gameWhenLoaded));
			} catch (final LoginException e) {
				System.err.println("The entered token is not valid!");
				token=null;
				continue;
			} catch (final IllegalArgumentException e) {
				System.err.println("There is no token entered!");
				token=null;
				continue;
			} catch (final InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
			break;
		}
	}
	private static OnlineStatus loadStatus(String statusStr, OnlineStatus defaultStatus) {
		OnlineStatus status=null;
		try {
			status = OnlineStatus.valueOf(statusStr.toUpperCase());
		} catch (Exception e) {
			
		}
		if (status==null||status==OnlineStatus.UNKNOWN) {
			status=OnlineStatus.fromKey(statusStr.toUpperCase());
			if (status==OnlineStatus.UNKNOWN) {
				status=defaultStatus;
			}
		}
		return status;
	}
	private static String getStringArgValue(String argStr,String defaultArg) {
		String newArg=null;
		try {
			newArg = argStr.substring(argStr.indexOf("=")+1);
		} catch (Exception e) {
		}
		if (newArg==null) {
			newArg=defaultArg;
		}
		return newArg;
	}
	/**
	 * adds Commands and Listeners
	 * @param ref The {@link Reflections} Object
	 * @param jdaBuilder The Builder of the JDA
	 */
	private static void addCommandsAndListeners(Reflections ref,JDABuilder jdaBuilder) {
		addAction(ref, BotCommand.class,(cmdAsAnnotation,annotatedAsObject)->{
    		BotCommand cmdAsBotCommand=(BotCommand)cmdAsAnnotation;
    		Command cmd=(Command)annotatedAsObject;
    		for (String alias : cmdAsBotCommand.value()) {
				CommandHandler.addCommand(alias.toLowerCase(), cmd);
			}
    	});
    	addAction(ref, BotListener.class,(cmdAsAnnotation,annotatedAsObject)->{
    		ListenerAdapter listener=(ListenerAdapter) annotatedAsObject;
			jdaBuilder.addEventListeners(listener);
    	});
	}
	/**
	 * invokes Method Objects of all Classes from that are annotated with a specified {@link Annotation}
	 * @param ref The {@link Reflections} Object that scanned the Classes
	 * @param jdaBuilder The Builder of the JDA
	 * @param annotClass The Class Object of the Annotation
	 * @param function
	 */
	private static void addAction(Reflections ref,Class<? extends Annotation> annotClass, BiConsumer<Annotation, Object> function) {
		for (Class<?> cl : ref.getTypesAnnotatedWith(annotClass,true)) {
            try {
				Object annotatedAsObject=cl.getDeclaredConstructor().newInstance();
				Annotation cmdAsAnnotation = cl.getAnnotation(annotClass);
				function.accept(cmdAsAnnotation, annotatedAsObject);
			} catch (InstantiationException e) {
				System.err.println(cl.getName()+" is annotated with @"+annotClass.getName()+" but cannot be instanciated");
			} catch (IllegalAccessException e) {
				System.err.println(cl.getName()+" is annotated with @"+annotClass.getName()+" but the no-args constructor is not visible");
			} catch (Throwable e) {
				System.err.println(cl.getName()+" is annotated with @"+annotClass.getName()+" but there was an unknown Error: "+e.getClass().getName()+": "+e.getCause());
			}
        }
    }
	/**
	 * loads the Plugins into an {@link ConfigurationBuilder}
	 * @param builder the {@link ConfigurationBuilder}
	 */
	private static void loadPlugins(ConfigurationBuilder builder) {
		List<URL> urls=new ArrayList<>();
		File pluginFolder=new File(STATIC.getSettingsDir(),"plugins");
		if (pluginFolder.exists()) {
			String[] filesInPluginFolder=pluginFolder.list();
			if (filesInPluginFolder!=null) {
				for (String pluginName : filesInPluginFolder) {
					try {
						if (pluginName.endsWith(".jar")) {
							File pluginFile=new File(pluginFolder,pluginName);
							if (pluginFile.isFile()) {
								urls.add(pluginFile.toURI().toURL());
							}
						}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
				URL[] urlArr=new URL[urls.size()];
				for (int i = 0; i < urlArr.length; i++) {
					urlArr[i]=urls.get(i);
				}
				builder.addUrls(urls);
				URLClassLoader loader=new URLClassLoader(urlArr);
				builder.addClassLoader(loader);
				LanguageController.setPluginLoader(loader);
			}
		}else {
			if(!pluginFolder.mkdir()) {
				System.err.println("cannot create directory: "+pluginFolder.getAbsolutePath());
			}
		}
	}
	/**
	 * get the Program arguments the Bot is started with.
	 * @return main args
	 */
	public static String[] getArgs() {
		return args.clone();
	}
	public static String getAdminId() {
		return adminId;
	}
	public static JDA getJda() {
		return jda;
	}
}
