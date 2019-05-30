package io.github.danthe1st.danbot1.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import io.github.danthe1st.danbot1.TestConfig;
import io.github.danthe1st.danbot1.TestUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;

@MainTest.AnnotationForTestAddAction
public class MainTest {
	private static JDA jda=null;
	private static final Object MONITOR=new Object();
	@BeforeAll
	public static void load() {
		synchronized (MONITOR) {
			if (jda==null) {
				init();
			}
		}
	}
	private static void init(){
		Main.main(new String[] {
				"game=Unit_testing",
				"token="+TestConfig.getToken(),
				"admin="+TestConfig.getAdminID(),
				"status=idle",
				"noevalsecurity",
				"settings="+TestConfig.getTestingSettingDir()
		});
		jda=Main.getJda();
	}
	@Test
	public void testBotData() {
		synchronized (MONITOR) {
			Main.getJda().shutdown();
			Main.main(new String[] {
					"game=Unit_testing",
					"token="+TestConfig.getToken(),
					"admin="+TestConfig.getSecondaryAdminID(),
					"status=idle",
					"noevalsecurity",
					"settings="+TestConfig.getTestingSettingDir()
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
		TestUtils.invokeNotAccessibleMethod(Main.class, "addAction",new Class<?>[] {
			Reflections.class,Class.class,BiConsumer.class
		}, null, new Reflections("io.github.danthe1st.danbot1"),cl,toExec);
		assertEquals(new ArrayList<Object>(Arrays.asList(getClass())),objects);
	}
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = ElementType.TYPE)
	public @interface AnnotationForTestAddAction{
		
	}
}
