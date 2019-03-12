package io.github.danthe1st.danbot1.core;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import java.util.Scanner;
import javax.security.auth.login.LoginException;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.listeners.BotListener;
import io.github.danthe1st.danbot1.util.STATIC;
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
	private static String game=System.getProperty("game");
	private static OnlineStatus status=OnlineStatus.ONLINE;
	private static String token=System.getProperty("token");
	private static final Scanner scan=new Scanner(System.in);
	private static String adminId="358291050957111296";
	private static String[] args;
	private static JDA jda=null;
	static {
		if (game==null) {
			game="with you";
		}
		Runtime.getRuntime().addShutdownHook(new Thread(new ScanCloser(scan)));
	}
	/**
	 * start-Method
	 * @param args 
	 * <pre> ?       show this help
	 * noStop  sets the Bot unstoppable
	 * game=&lt;name&gt;     Sets a game name
	 * token=&lt;Discord Bot token&gt; Sets the token of the Bot (you will be asked if it is not set)
	 * status=&lt;status&gt; Sets a Discord-Status for the Bot
	 * 		do_not_disturb, idle, invisible, online, offline, unknown
	 * admin=&lt;id&gt; sets the Bot admin to a specified user(you need the ID(ISnowfake ID) of the user)</pre>
	 */
	public static void main(final String[] args) {
		Main.args=args;
		for (String arg : args) {
			try {
				switch (arg.toLowerCase()) {
				case "?":
				case "help":
				case "-?":
				case "/help":
				case "-help":
					System.out.println("DanBot1 is a Discord Bot by Daniel Schmid\n"
							+ "See http://wwwmaster.at/daniel/data/DanBot1 for a list of all Commands\n"
							+ "possible arguments:\n"
							+ "\t?\tshow this help\n"
							+ "\tnoStop\tsets the Bot unstoppable\n"
							+ "\tgame=<name>\tSets a game name\n"
							+ "\ttoken=<Discord Bot token> Sets the token of the Bot (you will be asked if it is not set)\n"
							+ "\tstatus=<status>\tSets a Discord-Status for the Bot\n"
							+ 	"\t\tdo_not_disturb, idle, invisible, online, offline, unknown\n"
							+ "\tadmin=<id> sets the Bot admin to a specified user(you need the ID(ISnowfake ID) of the user)");
					return;
				default:
					if (arg.toLowerCase().startsWith("game=")) {
						game=arg.replaceAll("_", " ").substring(5);
						if (game.equals("")) {
							game="with you";
						}
						break;
					}
					else if (arg.toLowerCase().startsWith("token=")) {
						token=arg.substring(6);
					}
					else if (arg.toLowerCase().startsWith("admin=")) {
						String newAdminId=arg.substring(6);
						try {
							if (!newAdminId.equals("")) {
								Long.parseLong(adminId);
								adminId=newAdminId;
							}
						} catch (NumberFormatException e) {
						}
					}
					else if (arg.toLowerCase().startsWith("status=")) {
						String statusStr=arg.substring(7);
						switch (statusStr.toLowerCase()) {
						case "donotdisturb"://status=donotdisturb
						case "do_not_disturb":
							status=OnlineStatus.DO_NOT_DISTURB;
							break;
						case "idle":
							status=OnlineStatus.IDLE;
							break;
						case "invisible":
							status=OnlineStatus.INVISIBLE;
							break;
						case "online":
							status=OnlineStatus.ONLINE;
							break;
						case "offline":
							status=OnlineStatus.OFFLINE;
							break;
						case "?":
						case "unknown":
							status=OnlineStatus.UNKNOWN;
							break;
						default:
							break;
						}
						break;
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
			builder.setStatus(status);
			if (game==null) {
				game="with you";
			}
			builder.setActivity(Activity.playing(game));
			
			builder.setRequestTimeoutRetry(true);
			
			ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		    configurationBuilder.addUrls(ClasspathHelper.forClassLoader());
		    loadPlugins(configurationBuilder);
		    Reflections ref = new Reflections(configurationBuilder);
		    addCommandsAndListeners(ref,builder);
			try {
				jda=builder.build();
				jda.awaitReady();
				Console.runConsole(scan, jda);
				((JDAImpl) jda).getGuildSetupController().clearCache();
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
			}
			break;
		}
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
    		for (String alias : cmdAsBotCommand.aliases()) {
				CommandHandler.commands.put(alias.toLowerCase(), cmd);
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
				Object annotatedAsObject=cl.newInstance();
				if (annotatedAsObject==null) {
					System.err.println("no matching Constructor found for class"+cl.getName());
					break;
				}
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
				builder.addClassLoader(new URLClassLoader(urlArr));
			}
		}else {
			pluginFolder.mkdir();
		}
	}
	/**
	 * get the Program arguments the Bot is started with.
	 * @return main args
	 */
	public static String[] getArgs() {
		return args;
	}
	public static String getAdminId() {
		return adminId;
	}
	public static JDA getJda() {
		return jda;
	}
}
