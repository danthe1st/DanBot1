package commands.admin;

import java.time.OffsetDateTime;
import java.util.Formatter;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
/**
 * {@link Message} used for the sudo-Command
 * @author Daniel Schmid
 */
public class SudoMessage implements Message{
	private Message oldMsg;
	private String display;
	private String raw;
	private String stripped;
	private Member author;
	public SudoMessage(Message msg,String raw,String display,String stripped,Member author) {
		oldMsg=msg;
		this.display=display;
		this.raw=raw;
		this.stripped=stripped;
		this.author=author;
	}

	@Override
	public long getIdLong() {
		return oldMsg.getIdLong();
	}

	@Override
	public void formatTo(Formatter formatter, int flags, int width, int precision) {
		oldMsg.formatTo(formatter, flags, width, precision);
	}

	@Override
	public List<User> getMentionedUsers() {
		return oldMsg.getMentionedUsers();
	}

	@Override
	public List<TextChannel> getMentionedChannels() {
		return oldMsg.getMentionedChannels();
	}

	@Override
	public List<Role> getMentionedRoles() {
		return oldMsg.getMentionedRoles();
	}

	@Override
	public List<Member> getMentionedMembers(Guild guild) {
		return oldMsg.getMentionedMembers(guild);
	}

	@Override
	public List<Member> getMentionedMembers() {
		return oldMsg.getMentionedMembers();
	}

	@Override
	public List<IMentionable> getMentions(MentionType... types) {
		return oldMsg.getMentions(types);
	}

	@Override
	public boolean isMentioned(IMentionable mentionable, MentionType... types) {
		return oldMsg.isMentioned(mentionable, types);
	}

	@Override
	public boolean mentionsEveryone() {
		return oldMsg.mentionsEveryone();
	}

	@Override
	public boolean isEdited() {
		return oldMsg.isEdited();
	}


	@Override
	public User getAuthor() {
		return author.getUser();
	}

	@Override
	public Member getMember() {
		return oldMsg.getMember();
	}

	@Override
	public String getContentDisplay() {
		return display;
	}

	@Override
	public String getContentRaw() {
		return raw;
	}

	@Override
	public String getContentStripped() {
		return stripped;
	}

	@Override
	public List<String> getInvites() {
		return oldMsg.getInvites();
	}

	@Override
	public String getNonce() {
		return oldMsg.getNonce();
	}

	@Override
	public boolean isFromType(ChannelType type) {
		return oldMsg.isFromType(type);
	}

	@Override
	public ChannelType getChannelType() {
		return oldMsg.getChannelType();
	}

	@Override
	public boolean isWebhookMessage() {
		return oldMsg.isWebhookMessage();
	}

	@Override
	public MessageChannel getChannel() {
		return oldMsg.getChannel();
	}

	@Override
	public PrivateChannel getPrivateChannel() {
		return oldMsg.getPrivateChannel();
	}

	@Override
	public TextChannel getTextChannel() {
		return oldMsg.getTextChannel();
	}

	@Override
	public Category getCategory() {
		return oldMsg.getCategory();
	}

	@Override
	public Guild getGuild() {
		return oldMsg.getGuild();
	}

	@Override
	public List<Attachment> getAttachments() {
		return oldMsg.getAttachments();
	}

	@Override
	public List<MessageEmbed> getEmbeds() {
		return oldMsg.getEmbeds();
	}

	@Override
	public List<Emote> getEmotes() {
		return oldMsg.getEmotes();
	}

	@Override
	public List<MessageReaction> getReactions() {
		return oldMsg.getReactions();
	}

	@Override
	public boolean isTTS() {
		return oldMsg.isTTS();
	}

	@Override
	public MessageAction editMessage(CharSequence newContent) {
		return oldMsg.editMessage(newContent);
	}

	@Override
	public MessageAction editMessage(MessageEmbed newContent) {
		return oldMsg.editMessage(newContent);
	}

	@Override
	public MessageAction editMessageFormat(String format, Object... args) {
		return oldMsg.editMessageFormat(format, args);
	}

	@Override
	public MessageAction editMessage(Message newContent) {
		return oldMsg.editMessage(newContent);
	}

	@Override
	public AuditableRestAction<Void> delete() {
		return oldMsg.delete();
	}

	@Override
	public JDA getJDA() {
		return oldMsg.getJDA();
	}

	@Override
	public boolean isPinned() {
		return oldMsg.isPinned();
	}

	@Override
	public RestAction<Void> pin() {
		return oldMsg.pin();
	}

	@Override
	public RestAction<Void> unpin() {
		return oldMsg.unpin();
	}

	@Override
	public RestAction<Void> addReaction(Emote emote) {
		return oldMsg.addReaction(emote);
	}

	@Override
	public RestAction<Void> addReaction(String unicode) {
		return oldMsg.addReaction(unicode);
	}

	@Override
	public RestAction<Void> clearReactions() {
		return oldMsg.clearReactions();
	}

	@Override
	public MessageType getType() {
		return oldMsg.getType();
	}

	@Override
	public String getJumpUrl() {
		return oldMsg.getJumpUrl();
	}

	@Override
	public OffsetDateTime getTimeEdited() {
		return oldMsg.getTimeEdited();
	}
}
