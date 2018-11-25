package core;

import java.util.Scanner;

import javax.security.auth.login.LoginException;

import org.json.JSONObject;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import commands.BotCommand;
import commands.Command;
import listeners.AutoChannelHandler;
import listeners.AutoRoleListener;
import listeners.CommandListener;
import listeners.GuildChangeListener;
import listeners.ReadyListener;
import listeners.SpamProtectListener;
import listeners.VoiceListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.WebSocketCode;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
/**
 * <b>Main-Class</b><br>
 * Initiates the Bot
 * @author Daniel Schmid
 */
public class Main {
	private static boolean stoppable=true;
	private static String game=System.getProperty("game");
	private static OnlineStatus status=OnlineStatus.ONLINE;
	private static String token=System.getProperty("token");
	private static final Scanner scan=new Scanner(System.in);
	private static String adminId="358291050957111296";
	private static String[] args;
	static {
		if (game==null) {
			game="with you";
		}
		Runtime.getRuntime().addShutdownHook(new Thread(new ScanCloser(scan)));
	}
	
	public static void main(final String[] args) {
		Main.args=args;
		JDA jda = null;
		for (String arg : args) {
			try {
				switch (arg.toLowerCase()) {
				case "nostop":
				case "-nostop":
					stoppable=false;
					continue;
				case "?":
				case "help":
				case "-?":
				case "/help":
				case "-help":
					System.out.println("DanBot1 is a Discord Bot by Daniel Schmid\n"
							+ "See http://wwwmaster.at/daniel/data/DanBot1 for a list of all Commands\n"
							+ "possible arguments:\n"
							+ "\t?\tshow this help\n"
							+ "noStop\tsets the Bot unstoppable\n"
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
						adminId=arg.substring(6);
						if (adminId.equals("")) {
							adminId="358291050957111296";
						}
						try {
							Integer.parseInt(adminId);
						} catch (NumberFormatException e) {
							adminId="358291050957111296";
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
					System.out.println("Bitte geben Sie einen gültigen Bot-Token ein:");
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
				builder.setGame(Game.playing(game));
				
				builder.setRequestTimeoutRetry(true);
				
				initListeners(builder);
				
				
				try {
					jda=builder.build();
					addCommands();
					jda.awaitReady();
					jda.addEventListener(new AutoRoleListener(jda));
					loadRichPresence((JDAImpl) jda);
					Console.runConsole(scan, jda);
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
	 * adds Commands to the Command-Map
	 */
	private static void addCommands() {
		System.out.println("Scanning using Reflections:");
		 
        Reflections ref = new Reflections(ConfigurationBuilder.build());
        for (Class<?> cl : ref.getTypesAnnotatedWith(BotCommand.class)) {
            try {
				Object cmdAsObject=cl.newInstance();
				
				BotCommand cmdAsAnnotation = cl.getAnnotation(BotCommand.class);
				if (cmdAsObject instanceof Command) {
					Command cmd=(Command) cmdAsObject;
					CommandHandler.commands.put(cmdAsAnnotation.alias().toLowerCase(), cmd);
				}else {
					System.err.println(cl.getName()+" is annotated with @BotCommand but does not implement "+Command.class.getName());
				}
			} catch (InstantiationException e) {
				System.err.println(cl.getName()+" is annotated with @BotCommand but has no no-args-constructor or cannot be instanciated");
			} catch (IllegalAccessException e) {
				System.err.println(cl.getName()+" is annotated with @BotCommand but the no-args constructor is not visible");
			} catch (Throwable e) {
				System.err.println(cl.getName()+" is annotated with @BotCommand but there was an unknown Error: "+e.getClass().getName()+": "+e.getCause());

			}
        }
	}
	/**
	 * should load a RichPresence, but unfortunatly this doesn't work.
	 * @param jda the Representation of the <b>Java Discord API</b> as {@link JDAImpl}
	 */
	public static void loadRichPresence(JDAImpl jda) { //JDA object can be casted to a JDAImpl
        JSONObject obj = new JSONObject();
        JSONObject gameObj = new JSONObject();

        /* LAYOUT:
        * name
        * details
        * time elapsed (timestamps)
        * status
        */
        gameObj.put("name",  game);
        gameObj.put("type", 0); //1 if streaming
        gameObj.put("details", "waiting for commands");
        gameObj.put("state", "online");
        gameObj.put("timestamps", new JSONObject().put("start", 1508373056)); //somehow used for the time elapsed thing I assume, you can probably also set the end to make it show "xx:xx left"

        JSONObject assetsObj = new JSONObject();
        assetsObj.put("large_image", "danbot1_big"); //ID of large icon
        assetsObj.put("largeImageKey", "danbot1_big"); //ID of large icon
        assetsObj.put("large_text", "Large Text");

        assetsObj.put("small_image", "danbot1_small"); //ID of small icon
        assetsObj.put("smallImageKey", "danbot1_small"); //ID of small icon
//test with imageConfig???
        gameObj.put("assets", assetsObj);
        gameObj.put("application_id", "371042228891549707"); //Application ID

        obj.put("game", gameObj);
        obj.put("afk", jda.getPresence().isIdle());
        obj.put("status", jda.getPresence().getStatus().getKey());
        obj.put("since", System.currentTimeMillis());

        //System.out.println(obj);
       
        jda.getClient().send(new JSONObject()
                .put("d", obj)
                .put("op", WebSocketCode.PRESENCE).toString());
    }
	
	/**
	 * initiiert die Listener
	 * @param builder der JDABuilder
	 */
	private static void initListeners(final JDABuilder builder) {
		builder.addEventListener(new ReadyListener(),
				new VoiceListener(),
				new CommandListener(),
				new AutoChannelHandler(),
				new GuildChangeListener(),
				new SpamProtectListener());
	}
	/**
	 * tests if the Bot is stoppable
	 * @return <code>true</code> if it s stoppable
	 */
	public static boolean getStopable(){
		return stoppable;
	}
	/**
	 * get the Program arguments the Bot is started with.
	 * @return
	 */
	public static String[] getArgs() {
		return args;
	}
	public static String getAdminId() {
		return adminId;
	}
}
