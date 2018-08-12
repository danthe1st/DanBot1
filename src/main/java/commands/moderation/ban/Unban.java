package commands.moderation.ban;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

@XmlRootElement
@XmlSeeAlso(value=String[].class)
public class Unban{
	private String guild;
	private String[] user;
	public Unban(Guild guild,User... user) {
		this.setGuild(guild.getId());
		this.user=new String[user.length];
		for (int i = 0; i < user.length; i++) {
			this.user[i]=user[i].getId();
		}
	}
	public Unban() {
		setGuild(null);
		setUser(null);
	}
	public Guild guild(JDA jda) {
		return jda.getGuildById(getGuild());
	}
	public User[] user(JDA jda) {
		User[] users=new User[this.user.length];
		for (int i = 0; i < user.length; i++) {
			users[i]=jda.getUserById(user[i]);
		}
		return users;
	}
	public String getGuild() {
		return guild;
	}
	public void setGuild(String guild) {
		this.guild = guild;
	}
	public String[] getUser() {
		return user;
	}
	public void setUser(String[] user) {
		this.user = user;
	}
	
}