package io.github.danthe1st.danbot1.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.danthe1st.danbot1.TestConfig;
import io.github.danthe1st.danbot1.TestUtils;
import io.github.danthe1st.danbot1.commands.admin.SudoMessage;
import io.github.danthe1st.danbot1.core.Main;
import io.github.danthe1st.danbot1.core.MainTest;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class STATICTest {
	
	@BeforeAll
	public static void load() {
		MainTest.load();
	}
	@AfterAll
	public static void finish() {
		System.setSecurityManager(null);
	}
	@Test
	public void testEscapeDiscordMarkup() {
		assertEquals("\\*Hallo\\* \\_\\_Welt\\_\\_",STATIC.escapeDiscordMarkup("*Hallo* __Welt__"));
	}
	@Test
	public void testSendMessage() throws InterruptedException {
		assertTimeout(Duration.ofMillis(STATIC.INFO_TIMEOUT*4), () -> {
			JDA jda=Main.getJda();
			TextChannel tc=jda.getTextChannelById(TestConfig.getChannel());
			String msgContent="Hello, this is a Unit-Test Message that should be deleted automatically by the Unit-Test";
			STATIC.msg(tc, msgContent);
			Thread.sleep(STATIC.INFO_TIMEOUT*2);
			try {
				boolean hasMessage=false;
				for (Message msg : tc.getHistory().retrievePast(100).complete()) {
					if (msg.getAuthor().equals(jda.getSelfUser())) {
						for (MessageEmbed embed : msg.getEmbeds()) {
							String desc=embed.getDescription();
							if (desc!=null&&desc.equals(msgContent)) {
								msg.delete().queue();
								hasMessage=true;
								break;
							}
						}
					}
				}
				assertTrue(hasMessage);
			} catch (IllegalArgumentException e) {
			}
		});
	}
	@Test
	public void testSendErrorMessage() throws InterruptedException {
		assertTimeout(Duration.ofMillis(STATIC.INFO_TIMEOUT*4), () -> {
			JDA jda=Main.getJda();
			TextChannel tc=jda.getTextChannelById(TestConfig.getChannel());
			String msgContent="Hello, this is a Unit-Test error Message that should be deleted automatically by the System";
			STATIC.errmsg(tc, msgContent);
			Thread.sleep(STATIC.INFO_TIMEOUT*2);
			tc.getHistory().retrievePast(100);
			boolean hasMessage=false;
			for (Message msg : tc.getHistory().retrievePast(100).complete()) {
				if (msg.getAuthor().equals(jda.getSelfUser())) {
					for (MessageEmbed embed : msg.getEmbeds()) {
						String desc=embed.getDescription();
						if (desc!=null&&desc.equals(msgContent)) {
							msg.delete().queue();
							hasMessage=true;
						}
					}
					
				}
			}
			assertFalse(hasMessage);
		});
	}
	@Test
	public void testGetMembersFromMsg() {
		JDA jda=Main.getJda();
		Guild g=jda.getGuildById(TestConfig.getGuild());
		StringBuilder sb=new StringBuilder();
		sb.append("Hello ");//some String
		sb.append(jda.getSelfUser().getId());
		sb.append(" ");
		sb.append(jda.getUserById(TestConfig.getAdminID()).getName());
		Message msg=TestUtils.getMessage(jda.getTextChannelById(TestConfig.getChannel()),(message)->STATIC.getRolesFromMsg(message).isEmpty()&&message.getMentionedUsers().isEmpty());
		assertEquals(new HashSet<>(Arrays.asList(g.getMember(jda.getSelfUser()),g.getMemberById(TestConfig.getAdminID()))), STATIC.getMembersFromMsg(new SudoMessage(msg, sb.toString(), sb.toString(), sb.toString(), g.getMember(jda.getSelfUser()))));
		String msgContent="ufshuifgbs dsfhui ghuifg sfhdgu dfhs ug hdfs gusd gdhu ufd";
		assertEquals(Collections.emptySet(), STATIC.getMembersFromMsg(new SudoMessage(msg, msgContent,msgContent,msgContent, g.getMember(jda.getSelfUser()))));//nobody should have these Roles(else the Unit Test should fail
	}
	@Test
	public void testGetRolesFromMsg() {
		JDA jda=Main.getJda();
		Guild g=jda.getGuildById(TestConfig.getGuild());
		StringBuilder sb=new StringBuilder();
		sb.append("Hello ");//some String
		sb.append(g.getMember(jda.getSelfUser()).getRoles().get(0).getId());
		sb.append(" ");
		sb.append(g.getMemberById(TestConfig.getAdminID()).getRoles().get(0).getName());
		Message msg=TestUtils.getMessage(jda.getTextChannelById(TestConfig.getChannel()));
		assertEquals(Arrays.asList(g.getMember(jda.getSelfUser()).getRoles().get(0),g.getMemberById(TestConfig.getAdminID()).getRoles().get(0)), STATIC.getRolesFromMsg(new SudoMessage(msg, sb.toString(), sb.toString(), sb.toString(), g.getMember(jda.getSelfUser()))));
		String msgContent="ufshuifgbs dsfhui ghuifg sfhdgu dfhs ug hdfs gusd gdhu ufd";
		assertEquals(Collections.emptyList(), STATIC.getRolesFromMsg(new SudoMessage(msg, msgContent,msgContent,msgContent, g.getMember(jda.getSelfUser()))));//nobody should have these Roles(else the Unit Test should fail
	}
	@Test
	public void testPrefix() {
		JDA jda=Main.getJda();
		Guild g=jda.getGuildById(TestConfig.getGuild());
		String bkpPrefix=STATIC.getPrefix(g);
		assertNotNull(bkpPrefix);
		STATIC.setPrefix(g, "**");
		assertEquals("\\*\\*", STATIC.getPrefixEscaped(g));
		STATIC.setPrefix(g, bkpPrefix);
	}
	@Test
	public void testLoadDataShouldNotThrowException() {
		assertDoesNotThrow(()->STATIC.loadData(Main.getJda()));
	}
	@Test
	public void testGetCmdLogger() {
		JDA jda=Main.getJda();
		Guild g=jda.getGuildById(TestConfig.getGuild());
		String bkpCmdLogger=STATIC.getCmdLogger(g);
		assertNotNull(bkpCmdLogger);
		STATIC.setCmdLogger(g, "Hello World");
		assertEquals("Hello World", STATIC.getCmdLogger(g));
		STATIC.setCmdLogger(g, bkpCmdLogger);
	}
	@Test
	public void testGetServerData() {
		JDA jda=Main.getJda();
		Guild g=jda.getGuildById(TestConfig.getGuild());
		String gData=STATIC.getServerData(g);
		assertTrue(gData.contains(g.getName()));
		assertTrue(gData.contains(g.getId()));
	}
	@Test
	public void testCreateInvite() {
		JDA jda=Main.getJda();
		Guild g=jda.getGuildById(TestConfig.getGuild());
		assertNotNull(STATIC.createInvite(g));
	}
	@Test
	public void testSetGetSettingsDir() {
		assertTrue(new File(STATIC.getSettingsDir()).isDirectory());
		assertEquals(TestConfig.getTestingSettingDir(), STATIC.getSettingsDir());
		new File(STATIC.getSettingsDir()).delete();
		assertTrue(new File(STATIC.getSettingsDir()).isDirectory());
		String testFileName="__UnitTest__autodelete__";
		STATIC.setSettingsDir(testFileName);
		assertTrue(new File(testFileName).delete());
		STATIC.setSettingsDir(TestConfig.getTestingSettingDir());
	}
}
