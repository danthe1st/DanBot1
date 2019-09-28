package io.github.danthe1st.danbot1;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeAll;

import io.github.danthe1st.danbot1.core.MainTest;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public abstract class AbstractDanBot1Test {
	@BeforeAll
	public synchronized static void load() {
		MainTest.load();
	}
	
	//configs
	private static final Properties props=new Properties(System.getProperties());
	static {
		URL guildSpecURL = AbstractDanBot1Test.class.getClassLoader().getResource("GuildSpecific.properties");
		try(BufferedInputStream in=new BufferedInputStream(new FileInputStream(new File(guildSpecURL.toURI())))){
			props.load(in);
		} catch (IOException | URISyntaxException e) {
			
		}
		System.getenv().forEach((k,v)->{
			if(k.startsWith("DanBot1_testProp_")) {
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
	
	//Utils
	public static Message getMessage(TextChannel tc,Function<Message, Boolean> s) {
		for (Message msg : tc.getHistory().retrievePast(100).complete()) {
			if (s.apply(msg)) {
				return msg;
			}
		}
		return null;
	}
	public static Message getMessage(TextChannel tc) {
		return tc.getHistory().retrievePast(1).complete().get(0);
	}
	public static Message getMessage(TextChannel tc,Member member) {
		return getMessage(tc, msg->msg.getMember().equals(member));
	}
	public static Object invokeNotAccessibleMethod(Class<?> targetClass,String targetMethodName,Class<?>[] paramTypes, Object instanceOfClass, Object... params) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Method method = targetClass.getDeclaredMethod(targetMethodName, paramTypes);
		AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
            	method.setAccessible(true);
            	return null;
            }
        });
		return method.invoke(instanceOfClass, params);
	}
}
