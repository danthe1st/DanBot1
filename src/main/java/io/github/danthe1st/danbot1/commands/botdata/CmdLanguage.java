package io.github.danthe1st.danbot1.commands.botdata;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.util.Locale;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.LanguageController;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@BotCommand({ "lang","chlang","changelanguage" })
public class CmdLanguage implements Command {

	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.check(event, "changeLanguage");
	}
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if (args.length<1) {
			STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"missingArgs"));
			return;
		}
		switch (args[0]) {
		case "set":
			if (args.length<2) {
				STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"missingArgs"));
				return;
			}
			String[] localeArgs={"","",""};
			for (int i = 0; i < args.length-1&&i<localeArgs.length; i++) {
				localeArgs[i]=args[i+1];
			}
			Locale locale=new Locale(localeArgs[0],localeArgs[1],localeArgs[2]);
			LanguageController.setLocale(event.getGuild(), locale);
			break;
		case "get":
			STATIC.msg(event.getTextChannel(), translate(event.getGuild(),"LangPackIs")+LanguageController.getLocale(event.getGuild()));
			break;
		default:
			STATIC.errmsg(event.getTextChannel(), translate(event.getGuild(),"invalidArgs"));
			break;
		}
	}
	@Override
	public String help() {
		return "langHelp";
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.BOT_MODERATION;
	}

}
