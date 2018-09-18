package commands;

public enum CommandType {
	
	USER("User Command"),
	BOT_MODERATION("DanBot1 Moderation Command"),
	GUILD_MODERATION("Discord Server Moderation Command"),
	ADMIN("Admin Command - only bot-admin");
	
	private String name;
	private CommandType(String type) {
		name=type;
	}
	@Override
	public String toString() {
		return name;
	}
}
