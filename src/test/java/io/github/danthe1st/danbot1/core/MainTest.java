package io.github.danthe1st.danbot1.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import io.github.danthe1st.danbot1.AbstractDanBot1Test;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;

@MainTest.AnnotationForTestAddAction
public class MainTest  extends AbstractDanBot1Test{
	private static JDA jda=null;
	private static final Object MONITOR=new Object();
	@BeforeAll
	public static void load() {
		synchronized (MONITOR) {
			if (jda==null) {
				File settingsDir=new File(getTestingSettingDir());
				if (settingsDir.exists()&&settingsDir.isDirectory()) {
					try {
						FileUtils.cleanDirectory(settingsDir);
					} catch (IOException e) {
						System.err.println();
					}
				}
				init();
			}
		}
	}
	private static void init(){
		Main.main(new String[] {
				"game=Unit_testing",
				"token="+getToken(),
				"admin="+getAdminID(),
				"status=idle",
				"noevalsecurity",
				"settings="+getTestingSettingDir()
		});
		jda=Main.getJda();
	}
	@Test
	public void testBotData() {
		synchronized (MONITOR) {
			Main.getJda().shutdown();
			Main.main(new String[] {
					"game=Unit_testing",
					"token="+getToken(),
					"admin="+getSecondaryAdminID(),
					"status=idle",
					"noevalsecurity",
					"settings="+getTestingSettingDir()
			});
			assertEquals("362282283048239104", Main.getAdminId());
			assertEquals(OnlineStatus.IDLE, jda.getGuilds().get(0).getMember(jda.getSelfUser()).getOnlineStatus());
			Main.getJda().shutdown();
			init();
		}
	}
	@Test
	public void testAddAction() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		List<Class<?>> objects=new ArrayList<>();
		BiConsumer<Annotation, Object> toExec=(ann,obj)->{
			objects.add(obj.getClass());
		};
		Class<? extends Annotation> cl=AnnotationForTestAddAction.class;
		invokeNotAccessibleMethod(Main.class, "addAction",new Class<?>[] {
			Reflections.class,Class.class,BiConsumer.class
		}, null, new Reflections("io.github.danthe1st.danbot1"),cl,toExec);
		assertEquals(new ArrayList<Object>(Arrays.asList(getClass())),objects);
	}
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = ElementType.TYPE)
	public @interface AnnotationForTestAddAction{
		
	}
}
