package io.github.danthe1st.danbot1.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import io.github.danthe1st.danbot1.Secreds;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;

@MainTest.AnnotationForTestAddAction
public class MainTest {//TODO execute loadEverything() before all tests
	private static JDA jda=null;
	private static boolean initiated=false;
	@BeforeAll
	public static void load() {
		if (!initiated) {
			init();
			initiated=true;
		}
	}
	private static void init(){
		Main.main(new String[] {
				
				"game=Unit_testing",
				"token="+Secreds.token,
				"admin=362282283048239104",
				"status=idle"
		});
		jda=Main.getJda();
	}
	/*@AfterAll
	public static void unloadEverything(){
		jda.shutdown();
	}*/
	@Test
	public void testBotData() {
		String currentGame=jda.getGuilds().get(0).getMember(jda.getSelfUser()).getActivities().get(0).getName();
		assertEquals("Unit testing", currentGame);
		assertEquals("362282283048239104", Main.getAdminId());
		assertEquals(OnlineStatus.IDLE, jda.getGuilds().get(0).getMember(jda.getSelfUser()).getOnlineStatus());
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
	
	
	private Object invokeNotAccessibleMethod(Class<?> targetClass,String targetMethodName,Class<?>[] paramTypes, Object instanceOfClass, Object... params) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Method method = targetClass.getDeclaredMethod(targetMethodName, paramTypes);
		method.setAccessible(true);
		return method.invoke(instanceOfClass, params);
	}
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = ElementType.TYPE)
	private @interface AnnotationForTestAddAction{
		
	}
}
