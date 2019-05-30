package io.github.danthe1st.danbot1;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

public class TestConfig {
	private static final Properties props=new Properties(System.getProperties());
	static {
		URL guildSpecURL = TestConfig.class.getClassLoader().getResource("GuildSpecific.properties");
		try(BufferedInputStream in=new BufferedInputStream(new FileInputStream(new File(guildSpecURL.toURI())))){
			props.load(in);
		} catch (Exception e) {
			
		}
		System.getenv().forEach((k,v)->{
			if(k.startsWith("DanBot1.testProp.")) {
				props.setProperty(k.substring("DanBot1_testProp_".length()),v);
			}
		});
	}
	
	public static String getToken() {
		return props.getProperty("TOKEN");
	}
	public static String getAdminID() {
		return props.getProperty("ADMIN_ID");
	}
	public static String getSecondaryAdminID() {
		return props.getProperty("ADMIN_ID_SECONDARY");
	}
	public static String getChannel() {
		return props.getProperty("TESTING_CHANNEL");
	}
	public static String getGuild() {
		return props.getProperty("TESTING_GUILD");
	}
	public static String getGuildOwnerRole() {
		return props.getProperty("GUILD_OWNER_ROLE");
	}
	public static String getTestingSettingDir() {
		return props.getProperty("SETTINGS_DIR");
	}
}
