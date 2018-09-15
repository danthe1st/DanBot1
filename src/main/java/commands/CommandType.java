package commands;

public enum CommandType {
	ADMIN("Admin Command - only bot-admin"),
	BOT_MODERATION("DanBot1 Moderation Command"),
	USER("User Command"),
	GUILD_MODERATION("Discord Server Moderation Command");
	
	private String name;
	private CommandType(String type) {
		name=type;
	}
	@Override
	public String toString() {
		return name;
	}
}
