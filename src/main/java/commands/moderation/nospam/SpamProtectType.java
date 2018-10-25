package commands.moderation.nospam;
/**
 * enum for the type of punishment for spam
 * @author Daniel Schmid
 *
 */
public enum SpamProtectType {
	/**
	 * only delete the messages
	 */
	delete("delete"),
	/**
	 * kick the spammer
	 */
	kick("delete"),
	/**
	 * ban the spammer
	 */
	ban("delete");
	
	private String name;
	
	private SpamProtectType(String name){
		this.name=name;
	}

	public String getName() {
		return name;
	}
}
