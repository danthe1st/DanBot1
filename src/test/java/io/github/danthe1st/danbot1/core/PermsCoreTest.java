package io.github.danthe1st.danbot1.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.danthe1st.danbot1.TestConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PermsCoreTest {
	@BeforeAll
	public static void load() {
		MainTest.load();
	}
	@Test
	public void testCheckInvalidPermissionAndCheckOwner() {
		JDA jda=Main.getJda();
		TextChannel tc=jda.getTextChannelById("542372060366766091");
		MessageReceivedEvent event=getSomeMsgRescEvent(jda, getMessage(tc, true));
		assertTrue(PermsCore.checkOwner(event));
		assertTrue(PermsCore.check(event,someInvalidPermission));
		event=getSomeMsgRescEvent(jda, getMessage(tc, false));
		assertFalse(PermsCore.checkOwner(event));
		assertFalse(PermsCore.check(event,someInvalidPermission));
	}
	private final String someInvalidPermission="oaefisfdouhbfosfhaweioöfdifabdsifbssuaidfdasu";//some String(Users should not have the Permission explicitely)
	private MessageReceivedEvent getSomeMsgRescEvent(JDA jda,Message msg) {
		return new MessageReceivedEvent(jda, 0, msg);
	}
	private Message getMessage(TextChannel tc,boolean fromBotAdmin) {
		for (Message msg : tc.getHistory().retrievePast(100).complete()) {
			if (msg.getAuthor().getId().equals(Main.getAdminId())==fromBotAdmin) {
				return msg;
			}
		}
		return null;
	}
	private void checkPermission(String permName) {
		JDA jda=Main.getJda();
		Guild g=jda.getGuildById(TestConfig.TESTING_GUILD);
		TextChannel tc=jda.getTextChannelById(TestConfig.TESTING_CHANNEL);
		Map<String, String[]> bkpPerms=new HashMap<>(PermsCore.getPerms(g));
		PermsCore.setPerm(g, permName, new String[] {"*"});
		assertTrue(PermsCore.check(getSomeMsgRescEvent(jda, getMessage(tc, false)), permName,false));
		assertTrue(PermsCore.check(getSomeMsgRescEvent(jda, getMessage(tc, true)), permName,false));
		PermsCore.setPerm(g, permName, new String[] {});
		assertFalse(PermsCore.check(getSomeMsgRescEvent(jda, getMessage(tc, false)), permName,false));
		assertTrue(PermsCore.check(getSomeMsgRescEvent(jda, getMessage(tc, true)), permName,false));
		PermsCore.removePerm(g, permName);
		testAllEquals(bkpPerms,PermsCore.getPerms(g));
	}
	@Test
	public void checkValidPermission() {
		checkPermission(someInvalidPermission);
	}
	@Test
	public void checkEmptyPermission() {
		checkPermission("");
		checkPermission(null);
	}
	@Test
	public void testGetSetRemovePerm() {
		testGetSetRemovePerm(someInvalidPermission);
		testGetSetRemovePerm("");
	}
	private void testGetSetRemovePerm(String permName) {
		Guild g=Main.getJda().getGuildById(TestConfig.TESTING_GUILD);
		Map<String, String[]> bkpPerms=new HashMap<>(PermsCore.getPerms(g));
		assertTrue(Arrays.equals(new String[0], PermsCore.getPerm(g, permName)));
		PermsCore.setPerm(g, permName, new String[0]);
		bkpPerms.put(permName, new String[0]);
		assertTrue(Arrays.equals(new String[0], PermsCore.getPerm(g, permName)));
		testAllEquals(bkpPerms, PermsCore.getPerms(g));
		PermsCore.setPerm(g, permName, new String[] {"thisisaninvalidpermission"});
		bkpPerms.put(permName, new String[] {"thisisaninvalidpermission"});
		assertTrue(Arrays.equals(new String[] {"thisisaninvalidpermission"}, PermsCore.getPerm(g, permName)));
		testAllEquals(bkpPerms, PermsCore.getPerms(g));
		PermsCore.removePerm(g, permName);
		bkpPerms.remove(permName);
		assertTrue(Arrays.equals(new String[0], PermsCore.getPerm(g, permName)));
		testAllEquals(bkpPerms, PermsCore.getPerms(g));
	}
	@Test
	public void testResetPerms() {
		final Guild g=Main.getJda().getGuildById(TestConfig.TESTING_GUILD);
		Map<String, String[]> bkpPerms=new HashMap<>(PermsCore.getPerms(g));
		PermsCore.resetPerms(g);
		testAllEquals(PermsCore.getStdPermsAsIds(g), PermsCore.getPerms(g));
		
		bkpPerms.forEach((k,v)->{
			PermsCore.setPerm(g, k, v);
		});
		testAllEquals(bkpPerms, PermsCore.getPerms(g));
	}
	@Test
	public void testChRole() {
		Guild g=Main.getJda().getGuildById(TestConfig.TESTING_GUILD);
		Map<String, String[]> bkpPerms=new HashMap<>(PermsCore.getPerms(g));
		PermsCore.chRole(g, "Admin", "Owner");
		bkpPerms.forEach((k,v)->{
			List<String> roles=new ArrayList<>();
			for (String role : v) {
				if (!role.equals("Admin")) {
					roles.add(role);
				}
			}
			String[] newRoles=new String[roles.size()];
			for (int i = 0; i < newRoles.length; i++) {
				newRoles[i]=roles.get(i);
			}
			assertTrue(Arrays.equals(newRoles, PermsCore.getPerm(g, k)));
		});
		
		bkpPerms.forEach((k,v)->{
			PermsCore.removePerm(g, k);
			PermsCore.setPerm(g, k, v);
		});
	}
	
	@Test
	public void testgetRoleIDFromName() {
		assertEquals("*", PermsCore.getRoleIDFromName("*", Main.getJda().getGuildById(TestConfig.TESTING_GUILD)));
		assertNull(PermsCore.getRoleIDFromName(null, Main.getJda().getGuildById(TestConfig.TESTING_GUILD)));
		assertNull(PermsCore.getRoleIDFromName("", Main.getJda().getGuildById(TestConfig.TESTING_GUILD)));
		assertNull(PermsCore.getRoleIDFromName("afugshusdfhfudsgkbusdfhufsuöhf", Main.getJda().getGuildById(TestConfig.TESTING_GUILD)));
		assertEquals(TestConfig.GUILD_OWNER_ROLE, PermsCore.getRoleIDFromName("Owner", Main.getJda().getGuildById(TestConfig.TESTING_GUILD)));
	}
	@Test
	public void testgetRoleIDsFromNames() {
		assertTrue(Arrays.equals(new String[] {"*",TestConfig.GUILD_OWNER_ROLE}, PermsCore.getRoleIDsFromNames(new String[] {"*",null,"","afugshusdfhfudsgkbusdfhufsuöhf","Owner"}, Main.getJda().getGuildById(TestConfig.TESTING_GUILD))));
	}
	private void testAllEquals(Map<String, String[]> expected,Map<String, String[]> real) {
		real.forEach((k,v)->{
			assertTrue(Arrays.equals(v, expected.get(k)),"expected: "+Arrays.toString(v)+", got: "+Arrays.toString(expected.get(k))+", key: "+k);
		});
	}
	@Test
	public void testGetStdPermsAsIdsWithResetPerms() {
		Guild g=Main.getJda().getGuildById(TestConfig.TESTING_GUILD);
		Map<String, String[]> bkpPerms=PermsCore.getPerms(g);
		final Map<String, String[]> stdPerms=PermsCore.getStdPermsAsIds(g);
		PermsCore.resetPerms(g);
		Map<String, String[]> newPerms=PermsCore.getPerms(g);
		testAllEquals(stdPerms,newPerms);
		
		bkpPerms.forEach((k,v)->{
			PermsCore.removePerm(g, k);
			PermsCore.setPerm(g, k, v);
		});
	}
}
