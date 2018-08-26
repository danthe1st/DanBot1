package core;

import java.util.Scanner;

import javax.security.auth.login.LoginException;

import org.json.JSONObject;

import commands.CmdStop;
import commands.CmdRestart;
import commands.botdata.CmdAutoChannel;
import commands.botdata.CmdLogger;
import commands.botdata.CmdMotd;
import commands.botdata.CmdPerm;
import commands.botdata.CmdPrefix;
import commands.botdata.CmdVote;
import commands.dan1st.CmdEval;
import commands.dan1st.CmdReload;
import commands.dan1st.CmdSudo;
import commands.fun.CmdDice;
import commands.moderation.CmdAutoRole;
import commands.moderation.CmdKick;
import commands.moderation.CmdRole;
import commands.moderation.CmdVoiceKick;
import commands.moderation.ban.CmdBan;
import commands.moderation.ban.CmdTimeBan;
import commands.moderation.multicolor.CmdMultiColor;
import commands.music.CmdMusic;
import commands.utils.CmdClear;
import commands.utils.CmdClearPMs;
import commands.utils.CmdHelp;
import commands.utils.CmdPing;
import commands.utils.CmdSay;
import commands.utils.CmdSpam;
import commands.utils.CmdUnNick;
import commands.utils.CmdUser;
import listeners.AutoChannelHandler;
import listeners.AutoRoleListener;
import listeners.CommandListener;
import listeners.GuildChangeListener;
import listeners.ReadyListener;
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
	private static String nickname="DanBot1";
	private static String token=System.getProperty("token");
	private static final Scanner scan=new Scanner(System.in);
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
//							+ "noStop\tsets the Bot unstoppable\n"
						+ "\tgame=<name>\tSets a game name\n"
						+ "\tnickname=<name>\tsets a nickname in all Servers\n"
						+ "\ttoken=<Discord Bot token> Sets the token of the Bot (you will be asked if it is not set)\n"
						+ "\tstatus=<status>\tSets a Discord-Status for the Bot\n"
						+ 	"\t\tdo_not_disturb, idle, invisible, online, offline, unknown");
				
				
				return;
			default:
				if (arg.toLowerCase().startsWith("game=")) {
					game=arg.replaceAll("_", " ").substring(5);
					continue;
				}
				else if (arg.toLowerCase().startsWith("nickname=")||arg.toLowerCase().startsWith("name=")) {
					nickname=arg.replaceAll("_", " ").substring(arg.indexOf("="));
					continue;
				}
				else if (arg.toLowerCase().startsWith("token=")) {
					token=arg.substring(6);
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
					continue;
				}
				continue;
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
		CommandHandler.commands.put("ping", new CmdPing());
		CommandHandler.commands.put("say", new CmdSay());
		CommandHandler.commands.put("clear", new CmdClear());
		CommandHandler.commands.put("cls", CommandHandler.commands.get("clear"));
		CommandHandler.commands.put("m", new CmdMusic());
		CommandHandler.commands.put("music", CommandHandler.commands.get("m"));
		CommandHandler.commands.put("vote", new CmdVote());
		CommandHandler.commands.put("v", CommandHandler.commands.get("vote"));
		CommandHandler.commands.put("autochannel", new CmdAutoChannel());
		CommandHandler.commands.put("autoc", CommandHandler.commands.get("autochannel"));
		CommandHandler.commands.put("prefix", new CmdPrefix());
		CommandHandler.commands.put("stop", new CmdStop());
		CommandHandler.commands.put("help", new CmdHelp());
		CommandHandler.commands.put("perm", new CmdPerm());
		CommandHandler.commands.put("spam", new CmdSpam());
		CommandHandler.commands.put("kick", new CmdKick());
		CommandHandler.commands.put("ban", new CmdBan());
		CommandHandler.commands.put("role", new CmdRole());
		CommandHandler.commands.put("motd", new CmdMotd());
		CommandHandler.commands.put("cmdlogger", new CmdLogger());
		CommandHandler.commands.put("user", new CmdUser());
		CommandHandler.commands.put("restart", new CmdRestart());
		CommandHandler.commands.put("eval", new CmdEval());
		CommandHandler.commands.put("autorole", new CmdAutoRole());
		CommandHandler.commands.put("unnick", new CmdUnNick());
		CommandHandler.commands.put("sudo", new CmdSudo());
		CommandHandler.commands.put("multicolor", new CmdMultiColor());
		CommandHandler.commands.put("dice", new CmdDice());
		CommandHandler.commands.put("clearpm", new CmdClearPMs());
		CommandHandler.commands.put("vkick", new CmdVoiceKick());
		CommandHandler.commands.put("reload", new CmdReload());
		CommandHandler.commands.put("tban", new CmdTimeBan());//TODO add to webpage
		CommandHandler.commands.put("timeban", new CmdTimeBan());
	}
	
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
		builder.addEventListener(new ReadyListener(),new VoiceListener(),new CommandListener(),new AutoChannelHandler(),new GuildChangeListener());
	}
	public static boolean getStopable(){
		return stoppable;
	}
	public static String getNickname() {
		return nickname;
	}
	public static String[] getArgs() {
		return args;
	}
}
