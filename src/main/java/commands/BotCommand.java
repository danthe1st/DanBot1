package commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
@Repeatable(BotCommand.BotCommands.class)
public @interface BotCommand{
	String alias();
	
	@Target(value = ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface BotCommands{
		BotCommand[] value() default{};
	}
}
