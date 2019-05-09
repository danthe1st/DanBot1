package io.github.danthe1st.danbot1;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class TestUtils {
	private TestUtils() {}
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
		method.setAccessible(true);
		return method.invoke(instanceOfClass, params);
	}
}
