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
import commands.CommandType;
import core.PermsCore;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;
/**
 * Command for Polls in a Guild
 * @author Daniel Schmid
 */
public class CmdVote implements Command, Serializable{


	private static final long serialVersionUID = 1L;

	private static TextChannel channel;
	private static HashMap<Guild, Poll> polls=new HashMap<>();
	private static final String[] EMOTI= {":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:", ":keycap_ten:"};
	/**
	 * An internal class for a Poll<br>
	 * <b>A Poll contains:</b><br>
	 * the creator of the Poll(Discord User ID)<br>
	 * the Text of the Poll<br>
	 * a List of answer possibilities<br>
	 *  a number how often every answer was taken
	 * @author Daniel Schmid
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
	 * returns a Poll as {@link EmbedBuilder}
	 * 
	 * @param poll The Poll
	 * @param g The Guild(Discord-Server)
	 * @return Poll as {@link EmbedBuilder}
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
	 * creates a new Poll(errormessage if there is a poll wich already exists
	 * @param args Arguments-Arrey of the Command
	 * @param event {@link MessageReceivedEvent} of the Command
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
	 * votes for a Poll
	 * @param args Arguments-Arrey of the Command
	 * @param event {@link MessageReceivedEvent} of the Command
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
	 * sends a Message with the date of the current Poll(of the {@link Guild})
	 * @param event The {@link MessageReceivedEvent} of the Command
	 */
	private void voteStats(final MessageReceivedEvent event) {
		if (!polls.containsKey(event.getGuild())) {
			STATIC.errmsg(event.getTextChannel(), "There is currently no poll running to show");
			return;
		}
		channel.sendMessage(getParsedPoll(polls.get(event.getGuild()), event.getGuild()).build()).queue();
		
	}
	/**
	 * ends the Running Poll
	 * @param event The {@link MessageReceivedEvent} of the Command
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
	 * saves a Poll
	 * @param guild The Guild(Discord-Server)
	 * @throws IOException If an I/O error has occurred.
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
	 * loads a Poll and returns it
	 * @param g The Guild(Discord-Server)
	 * @return The Poll
	 * @throws IOException If an I/O error has occurred.
	 * @throws ClassNotFoundException Class Poll cannot be found.
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
	 * loads all Polls
	 * @param jda The JDA Class
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
	public CommandType getCommandType() {
		return CommandType.USER;
	}
}
