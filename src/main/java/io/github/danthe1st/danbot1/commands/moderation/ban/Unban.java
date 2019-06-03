package io.github.danthe1st.danbot1.commands.moderation.ban;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
/**
 * Wrapper-Class for an unban
 * @author Daniel Schmid
 */
@XmlRootElement
@XmlSeeAlso(value=String[].class)
public class Unban{
	private String guild;
	private String[] user;
	public Unban(Guild guild,User... user) {
		if (user==null) {
			throw new IllegalArgumentException("no user defined");
		}
		this.setGuild(guild.getId());
		this.user=new String[user.length];
		for (int i = 0; i < user.length; i++) {
			this.user[i]=user[i].getId();
		}
	}
	public Unban() {
		setGuild(null);
		setUsers(new String[0]);
	}
	/**
	 * gets the Guild of the unban
 	 * @param jda the JDA Object
	 * @return the Guild(Discord-Server)
	 */
	public Guild guild(JDA jda) {
		return jda.getGuildById(getGuild());
	}
	public String[] getUsers() {
		return Arrays.copyOf(user,user.length);
	}
	public String getGuild() {
		return guild;
	}
	public void setGuild(String guild) {
		this.guild = guild;
	}
	public void setUsers(String[] users) {
		this.user = users.clone();
	}
	
}