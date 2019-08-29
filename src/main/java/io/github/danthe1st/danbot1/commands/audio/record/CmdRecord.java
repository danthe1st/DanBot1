package io.github.danthe1st.danbot1.commands.audio.record;


import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.commands.audio.AudioHolderController;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@BotCommand(aliases = "record")
public class CmdRecord implements Command{
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return PermsCore.checkOwner(event);
	}
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if (args.length<1) {
			STATIC.errmsg(event.getTextChannel(), "not anough arguments");
			return;
		}
		switch (args[0].toLowerCase()) {
		case "start":
			VoiceChannel vc=event.getGuild().getMember(event.getAuthor()).getVoiceState().getChannel();
			if (vc==null) {
				STATIC.errmsg(event.getTextChannel(), "You must be in a Voice Channel to do this!");
				return;
			}
			Recorder rec=Recorder.getInstance(event.getGuild());
			AudioHolderController.reserverHolder(event.getGuild(), rec);
			event.getGuild().getAudioManager().openAudioConnection(vc);
			event.getGuild().getAudioManager().setReceivingHandler(rec);
			break;
		case "stop":
			Recorder.getInstance(event.getGuild()).onEverybodyLeave();
			break;
		default:
			break;
		}
	}

	@Override
	public String help(String prefix) {
		return "starts/stops recording in a Voice Channel\n"
				+ "(see Permission *record* in Command perm get)\n"
				+ "*Syntax*: "+prefix+"record start/stop";
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.ADMIN;
	}
	
}
