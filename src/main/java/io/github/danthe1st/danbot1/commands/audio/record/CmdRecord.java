package io.github.danthe1st.danbot1.commands.audio.record;


import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.commands.audio.AudioHolderController;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.LanguageController;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("record")
public class CmdRecord implements Command{
	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return PermsCore.check(event, "record");
	}
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if (args.length<1) {
			STATIC.errmsg(event.getChannel(), "missingArgs");
			return;
		}
		VoiceChannel vc=event.getGuild().getMember(event.getAuthor()).getVoiceState().getChannel();
		switch (args[0].toLowerCase()) {
		case "start":
			if (vc==null) {
				STATIC.errmsg(event.getChannel(), LanguageController.translate(event.getGuild(), "mustBeInVC"));
				return;
			}
			Recorder rec=Recorder.getInstance(event.getGuild());
			AudioHolderController.reserverHolder(event.getGuild(), rec);
			event.getGuild().getAudioManager().openAudioConnection(vc);
			event.getGuild().getAudioManager().setReceivingHandler(rec);
			break;
		case "stop":
			Recorder.getInstance(event.getGuild()).onEverybodyLeave(vc);
			break;
		default:
			break;
		}
	}

	@Override
	public String help() {
		return "recordHelp";
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.USER;
	}
	
}
