package io.github.danthe1st.danbot1.core;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import io.github.danthe1st.danbot1.AbstractDanBot1Test;
import io.github.danthe1st.danbot1.commands.admin.SudoMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandParserTest  extends AbstractDanBot1Test{
	@Test
	public void testCommands() {
		testNormalCommand("--say Hello World", new String[] {"Hello", "World"});
		testNormalCommand("--say \"Hello World\"", new String[] {"Hello World"});
		testNormalCommand("--say \"Hello World\" Test", new String[] {"Hello World","Test"});
		String someCommandArgs="fsd a fg sif  hdhg duish ud dfhd";
		testNormalCommand("*fsdfds "+someCommandArgs, someCommandArgs.split(" "),"*");
		testNormalCommand("--say \"Hello\" World", new String[] {"\"Hello\"","World"});
	}
	public void testNormalCommand(String cmd, String[] expected) {
		Member member=Main.getJda().getGuildById(getGuild()).getMemberById(Main.getAdminId());
		CommandParser.CommandContainer container=CommandParser.parser(new GuildMessageReceivedEvent(Main.getJda(), 0, new SudoMessage(getMessage(Main.getJda().getTextChannelById(getChannel()),member),cmd, cmd, cmd, member)));
		assertTrue(Arrays.equals(container.args, expected));
	}
	public void testNormalCommand(String cmd, String[] expected,String prefix) {
		Member member=Main.getJda().getGuildById(getGuild()).getMemberById(Main.getAdminId());
		CommandParser.CommandContainer container=CommandParser.parser(new GuildMessageReceivedEvent(Main.getJda(), 0, new SudoMessage(getMessage(Main.getJda().getTextChannelById(getChannel()),member),cmd, cmd, cmd, member)));
		assertTrue(Arrays.equals(container.args, expected));
	}
}
