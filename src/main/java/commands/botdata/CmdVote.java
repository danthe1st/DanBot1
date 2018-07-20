package commands.botdata;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import commands.Command;
import core.PermsCore;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command f. Umfragen innerhalb eines Discord-Servers
 * @author Daniel Schmid
 *
 */
public class CmdVote implements Command, Serializable{


	private static final long serialVersionUID = 1L;

	private static TextChannel channel;
	private static HashMap<Guild, Poll> polls=new HashMap<>();
	private static final String[] EMOTI= {":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:", ":keycap_ten:"};
	/**
	 * Interne, Serialisierbare Klasse f. eine Umfrage<br>
	 * <b>Eine Umfrage besitzt:</b><br>
	 * den Ersteller der Umfrage(Discord-ID)<br>
	 * den Text der Umfrage<br>
	 * Alle Antwortm�glichkeiten<br>
	 * wie oft jede Antwort ausgew�hlt wurde
	 * @author Daniel Schmid
	 *
	 */
	private class Poll implements Serializable{
		private static final long serialVersionUID = 1L;
		private final String creator;
		private final String heading;
		private final List<String> answers;
		private final HashMap<String, Integer> votes;
		public Poll(final Member creator, final String heading, final List<String> answers) {
			this.creator=creator.getUser().getId();
			this.heading=heading;
			this.answers=answers;
			this.votes=new HashMap<>();
		}
		
		private Member getCreator(final Guild g) {
			return g.getMember(g.getJDA().getUserById(creator));
		}
	}
	/**
	 * gibt eine Umfrage als EmbedBuilder zur�ck(�hnlich wie toString)
	 * @param poll Die Umfrage selbst
	 * @param g Der Discord-Server
	 * @return Umfrage als EmbedBuilder
	 */
	private EmbedBuilder getParsedPoll(final Poll poll, final Guild g) {
		final StringBuilder ansStr=new StringBuilder();
		final AtomicInteger count=new AtomicInteger();
		poll.answers.forEach(s->{
			final long votescount = poll.votes.keySet().stream().filter(k -> poll.votes.get(k).equals(count.get() + 1)).count();
            ansStr.append(EMOTI[count.get()] + "  -  " + s + "  -  Votes: `" + votescount + "` \n");
            count.addAndGet(1);
			
		});
		return new EmbedBuilder()
				.setAuthor(poll.getCreator(g).getEffectiveName()+"\'s Poll", null, poll.getCreator(g).getUser().getAvatarUrl())
				.setDescription(":pencil:   "+poll.heading+"\n\n"+ansStr.toString())
				.setFooter("Enter \'"+STATIC.getPrefix(g)+"vote v <number>\' to vote!", null)
				.setColor(Color.cyan);
	}
	/**
	 * erstellt eine neue Umfrage(Fehlernachricht wenn schon Umfrage vorhanden)
	 * @param args Das arg-Array des Commands
	 * @param event Das MessageReceivedEvent des Commands
	 */
	private void craetePoll(final String[] args, final MessageReceivedEvent event) {
		if (polls.containsKey(event.getGuild())) {
			STATIC.msg(event.getTextChannel(), "There is already a poll running on this guild");
			return;
		}
		final String argsStr=String.join(" ", new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
		final List<String> content=Arrays.asList(argsStr.split("\\|"));
		final String heading=content.get(0);
		final List<String> answers=new ArrayList<>(content.subList(1, content.size()));
		final Poll poll=new Poll(event.getMember(), heading, answers);
		polls.put(event.getGuild(), poll);
		channel.sendMessage(getParsedPoll(poll, event.getGuild()).build()).queue();
		
	}
	/**
	 * w�hlt f. eine Umfrage
	 * @param args Das arg-Array des Commands
	 * @param event Das MessageReceivedEvent des Commands
	 */
	private void votePoll(final String[] args, final MessageReceivedEvent event) {
		if (!polls.containsKey(event.getGuild())) {
			STATIC.errmsg(event.getTextChannel(), "There is currently no poll running to vote for");
			return;
		}
		final Poll poll=polls.get(event.getGuild());
		int vote;
		try {
			vote = Integer.parseInt(args[1]);
			if (vote>poll.answers.size()) {
				throw new NumberFormatException("Number is out of range ");
			}
		} catch (final Exception e) {
			STATIC.errmsg(event.getTextChannel(), "Please enter a valic number to vote for!");
			return;
		}
		if (poll.votes.containsKey(event.getAuthor().getId())) {
			STATIC.errmsg(event.getTextChannel(), "Sorry, but you can only **vote** once for a Poll");
			return;
		}
		poll.votes.put(event.getAuthor().getId(), vote);
		polls.replace(event.getGuild(), poll);
		event.getMessage().delete().queue();
	}
	/**
	 * sendet eine Nachricht mit d. Daten der Vote(der Guild)
	 * @param event Das MessageReceivedEvent des Commands
	 */
	private void voteStats(final MessageReceivedEvent event) {
		if (!polls.containsKey(event.getGuild())) {
			STATIC.errmsg(event.getTextChannel(), "There is currently no poll running to show");
			return;
		}
		channel.sendMessage(getParsedPoll(polls.get(event.getGuild()), event.getGuild()).build()).queue();
		
	}
	/**
	 * beendet eine Umfrage
	 * @param event Das MessageReceivedEvent des Commands
	 */
	private void closeVote(final MessageReceivedEvent event) {
		if (!polls.containsKey(event.getGuild())) {
			STATIC.errmsg(event.getTextChannel(), "There is currently no poll running to close");
			return;
		}
		final Poll poll=polls.get(event.getGuild());
		
		polls.remove(event.getGuild());
		channel.sendMessage(getParsedPoll(poll, event.getGuild()).build()).queue();
		STATIC.msg(event.getTextChannel(), "Poll closed by "+ event.getAuthor().getAsMention()+".");
	}
	/**
	 * speichert eine Umfrage
	 * @param guild Der Discord-Server
	 * @throws IOException
	 */
	private void savePoll(final Guild guild) throws IOException {
		if(!polls.containsKey(guild)) {
			final String saveFile=STATIC.getSettingsDir()+"/"+guild.getId()+"/vote.dat";
			final File f=new File(saveFile);
			f.delete();
			return;
		}
		final String saveFile=STATIC.getSettingsDir()+"/"+guild.getId()+"/vote.dat";
		final Poll poll=polls.get(guild);
		
		 
		final FileOutputStream fos=new FileOutputStream(saveFile);
		
		final ObjectOutputStream oos=new ObjectOutputStream(fos);
		
		oos.writeObject(poll);
		oos.close();
	}
	/**
	 * l�dt die Umfrage der Guild und gibt diese zur�ck
	 * @param g Die Guild(Discord-Server)
	 * @return Die Umfrage
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static Poll getPoll(final Guild g) throws IOException, ClassNotFoundException {
		if(polls.containsKey(g)) {
			return null;
		}
		final String saveFile=STATIC.getSettingsDir()+"/"+g.getId()+"/vote.dat";
		
		final FileInputStream fis=new FileInputStream(saveFile);
		final ObjectInputStream ois=new ObjectInputStream(fis);
		final Poll out=(Poll)ois.readObject();
		ois.close();
		return out;
	}
	/**
	 * L�dt alle Umfragen
	 * @param jda Die Java Discord API
	 */
	public static void loadPolls(final JDA jda) {
		jda.getGuilds().forEach(g->{
			final File f=new File(STATIC.getSettingsDir()+"/"+g.getId()+"/vote.dat");
			if(f.exists()) {
				try {
					polls.put(g, getPoll(g));
				} catch (IOException|ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * Der Befehl selbst(siehe help)
	 */
	@Override
	public void action(final String[] args, final MessageReceivedEvent event) {
		if(!PermsCore.check(event, "vote")) {
			return;
		}
		channel=event.getTextChannel();
		if (args.length<1) {
			STATIC.errmsg(event.getTextChannel(), help(STATIC.getPrefixExcaped(event.getGuild())));
			return;
		}
		switch (args[0]) {
		case "create":
			if(!PermsCore.check(event, "vote.create")) {
				return;
			}
			craetePoll(args, event);
			break;
		case "vote":
		case "v":
			if(!PermsCore.check(event, "vote.vote")) {
				return;
			}
			votePoll(args, event);
			break;
		case "stats":
		case "s":
			if(!PermsCore.check(event, "vote.stats")) {
				return;
			}
			voteStats(event);
			break;
		case "close":
		case "c":
			if(!PermsCore.check(event, "vote.close")) {
				return;
			}
			closeVote(event);
			try {
				savePoll(event.getGuild());
			} catch (final IOException e1) {
				e1.printStackTrace();
			}
			break;
		default:
			STATIC.errmsg(channel, help(STATIC.getPrefixExcaped(event.getGuild())));
			return;
		}
		polls.forEach((guild, poll)->{
			final File path=new File(STATIC.getSettingsDir()+"/"+guild.getId()+"/");
			if(!path.exists()) {
				path.mkdirs();
			}
			try {
				savePoll(guild);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		});
	}


	/**
	 * hilfe: gibt Hilfe zu diesem Command als String zur�ck
	 */
	@Override
	public String help(String prefix) {
		return "Creates a poll OR\n"
				+ "vote for the poll OR\n"
				+ "show the stats of the poll OR\n"
				+ "close the poll\n"
				+ "(see *vote* Permissions in Command perm get)\n"
				+"*Syntax*: "+prefix+"vote create <Poll: Question|answer 1|answer 2|...>, v/vote <number of answer you want to vote>, stats/s, close";
	}
	@Override
	public String getCommandType() {
		return CMD_TYPE_USER;
	}
}
