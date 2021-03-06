package io.github.danthe1st.danbot1.commands.botdata;

import static io.github.danthe1st.danbot1.util.LanguageController.translate;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import io.github.danthe1st.danbot1.commands.BotCommand;
import io.github.danthe1st.danbot1.commands.Command;
import io.github.danthe1st.danbot1.commands.CommandType;
import io.github.danthe1st.danbot1.core.PermsCore;
import io.github.danthe1st.danbot1.util.MapWrapper;
import io.github.danthe1st.danbot1.util.STATIC;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
/**
 * Command for AutoChannels
 * @author Daniel Schmid
 */
@BotCommand({"autoc","autochannel"})
public class CmdAutoChannel implements Command, Serializable {
	private static final long serialVersionUID = 1L;
	
	private static HashMap<VoiceChannel, Guild> autoChannels=new HashMap<>();
	
	public static Map<VoiceChannel, Guild> getAutoChannels(){
		return autoChannels;
	}
	public static VoiceChannel getVoiceChannel(final String id,final Guild g) {
		return g.getVoiceChannelById(id);
	}
	private static Guild getGuild(final String id, final JDA jda) {
		return jda.getGuildById(id);
	}
	/**
	 * sets an autochannel
	 * @param id ID of the autochannel
	 * @param g the Guild(Discord-server)
	 * @param tc The {@link TextChannel} where responses should be sent
	 */
	private void setChannel(final String id, final Guild g, final TextChannel tc) {
		final VoiceChannel vc=getVoiceChannel(id, g);
		if (vc==null) {
			STATIC.errmsg(tc, translate(g,"errInvalidChannelID"));
		}
		else if(autoChannels.containsKey(vc)) {
			STATIC.errmsg(tc, translate(g,"alreadyAutoChannel"));
		}
		else {
			autoChannels.put(vc, g);
			save();
			STATIC.msg(tc, String.format(translate(g,"setAsAutoChannel"), vc.getName()));
		}
		
	}
	/**
	 * deletes an autochannel
	 * @param id ID of the autochannels
	 * @param g The Guild(Discord-server)
	 * @param tc The {@link TextChannel} where responses should be sent
	 */
	private void unsetChan(final String id, final Guild g, final TextChannel tc) {
		final VoiceChannel vc=getVoiceChannel(id, g);
		if(vc==null) {
			STATIC.errmsg(tc, translate(g,"errInvalidChannelID"));
		}
		else if (!autoChannels.containsKey(vc)) {
			STATIC.errmsg(tc, translate(g,"noAutoChannel"));
		}
		else {
			autoChannels.remove(vc);
			save();
			STATIC.msg(tc, String.format(translate(g,"unsetAsAutoChannel"), vc.getName()));
		}
	}
	/**
	 * deletes an autochannel
	 * @param vc Der autochannel to delete as {@link VoiceChannel}
	 */
	public static void unsetChan(final VoiceChannel vc) {
		autoChannels.remove(vc);
		save();
	}
	/**
	 * sends a Message with all autochannels in one {@link Guild} in a {@link TextChannel}
	 * @param g the Guild(Discord Server)
	 * @param tc Der {@link TextChannel} for responses
	 */
	private void listChans(final TextChannel tc) {
		final StringBuilder sb=new StringBuilder().append(translate(tc.getGuild(),"listAutoChannelsTitle"));
		autoChannels.keySet().stream()
			.filter(vc->autoChannels.get(vc).equals(tc.getGuild()))
			.forEach(vc->sb.append(String.format(":white_small_square: \'%s\' *(%s)%n", vc.getName(), vc.getId())));
		
		STATIC.msg(tc, sb.toString());
	}
	/**
	 * saves all autochannels
	 */
	private static void save() {
		final HashMap<String, String> out=new HashMap<>();
		autoChannels.forEach((vc,g)->out.put(vc.getId(), g.getId()));
		File file=new File(STATIC.getSettingsDir()+"/autochannels.xml");
		if (!file.exists()) {
			try {
				Files.createFile(file.toPath());
			} catch (IOException e) {
				//ignore
			}
		}
		try {
			JAXBContext context = JAXBContext
			        .newInstance(MapWrapper.class);
			Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	        m.marshal(new MapWrapper<>(out), file);
		} catch (JAXBException e) {
			//ignore
		}
	}
	/**
	 * loads all autochannels
	 * @param jda the {@link JDA}
	 */
	public static void load(final JDA jda) {
		try {
			final File file=new File(STATIC.getSettingsDir()+"/autochannels.xml");
			JAXBContext context=JAXBContext.newInstance(MapWrapper.class);
			 Unmarshaller um = context.createUnmarshaller();

		        // Reading XML from the file and unmarshalling.
			 @SuppressWarnings("unchecked")
			MapWrapper<String,String> data = (MapWrapper<String,String>) um.unmarshal(file);
		       Map<String,String> out=data.getData();
			 
		       out.forEach((vId, gId)->{
					final Guild g=getGuild(gId, jda);
					try {
						autoChannels.put(getVoiceChannel(vId, g), g);
					} catch (Exception e) {
						//ignore
					}
				});
		} catch (JAXBException e) {
			//ignore
		}
	}
	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return PermsCore.check(event, "autoChannel");
	}
	@Override
	public void action(final String[] args, final GuildMessageReceivedEvent event) {
		final Guild g=event.getGuild();
		final TextChannel tc=event.getChannel();
		if(args.length<1) {
			STATIC.errmsg(tc, translate(event.getGuild(),help()).replace("--",STATIC.getPrefixEscaped(event.getGuild())));
			return;
		}
		switch (args[0]) {
		case "list":
			listChans(tc);
			break;
		case "set":
		case "add":
			if(args.length<2) {
				STATIC.errmsg(tc, translate(event.getGuild(),help()).replace("--",STATIC.getPrefixEscaped(event.getGuild())));
				return;
			}
			setChannel(args[1], g, tc);
			break;
		case "remove":
		case "unset":
		case "rem":
		case "delete":
		case "del":
			if(args.length<2) {
				STATIC.errmsg(tc, translate(event.getGuild(),help()).replace("--",STATIC.getPrefixEscaped(event.getGuild())));
				return;
			}
			unsetChan(args[1], g, tc);
			break;
		default:
			STATIC.errmsg(tc, translate(event.getGuild(),help()).replace("--",STATIC.getPrefixEscaped(event.getGuild())));
			break;
		}
		
	}
	@Override
	public String help() {
		return "autoChannelHelp";
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.BOT_MODERATION;
	}
}
