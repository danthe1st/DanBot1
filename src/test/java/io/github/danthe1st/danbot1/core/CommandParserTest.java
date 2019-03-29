package io.github.danthe1st.danbot1.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.danthe1st.danbot1.TestUtils;
import io.github.danthe1st.danbot1.commands.admin.SudoMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandParserTest {
	@BeforeAll
	public static void load() {
		MainTest.load();
	}
	@Test
	public void testCommands() {
		testNormalCommand("--say Hello World", new String[] {"Hello", "World"});
		testNormalCommand("--say \"Hello World\"", new String[] {"Hello World"});
		testNormalCommand("--say \"Hello World\" Test", new String[] {"Hello World","Test"});
		String someCommandArgs="fsd a fg sif  hdhg duish ud dfhd";
		testNormalCommand("*fsdfds "+someCommandArgs, someCommandArgs.split(" "),"*");
	}
	public void testNormalCommand(String cmd, String[] expected) {
		Member member=Main.getJda().getGuilds().get(0).getMemberById(Main.getAdminId());
		CommandParser.CommandContainer container=CommandParser.parser(new MessageReceivedEvent(Main.getJda(), 0, new SudoMessage(TestUtils.getMessage(Main.getJda().getTextChannelById("542372060366766091"),member),cmd, cmd, cmd, member)));
		assertTrue(Arrays.equals(container.args, expected));
	}
	public void testNormalCommand(String cmd, String[] expected,String prefix) {
		Member member=Main.getJda().getGuilds().get(0).getMemberById(Main.getAdminId());
		CommandParser.CommandContainer container=CommandParser.parser(new MessageReceivedEvent(Main.getJda(), 0, new SudoMessage(TestUtils.getMessage(Main.getJda().getTextChannelById("542372060366766091"),member),cmd, cmd, cmd, member)));
		assertTrue(Arrays.equals(container.args, expected));
	}
}
