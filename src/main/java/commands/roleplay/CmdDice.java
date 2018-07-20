package commands.roleplay;

import java.awt.Color;
import java.util.Random;

import commands.Command;
import core.PermsCore;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;

public class CmdDice implements Command{
	private static Random rand=new Random();
	/**
	 * Der Befehl selbst(siehe help)
	 */
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if(!PermsCore.check(event, "dice")) {
			return;
		}
		int numRolls=1;
		if (args.length>0) {
			try {
				numRolls=Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				STATIC.errmsg(event.getTextChannel(), "argument 1 is no integer(number)");
			}
		}
		
		STATIC.msg(event.getTextChannel(), "Rolling the Dice...",Color.GRAY, true);
		int erg=0;
		StringBuilder valuesBuilder=new StringBuilder("[");
		for (int i = 0; i < numRolls; i++) {
			int value=rand.nextInt(6)+1;
			erg+=value;
			valuesBuilder.append(value);
			if (i<numRolls-1) {
				valuesBuilder.append(" ");
			}
			else {
				valuesBuilder.append("]");
			}
		}
		
		
		STATIC.msg(event.getTextChannel(), "You got "+erg+valuesBuilder);
	}
	/**
	 * hilfe: gibt Hilfe zu diesem Command als String zurück
	 */
	@Override
	public String help(String prefix) {
		return "Rolls the dice one time or often.\n"
				+ "(see Permission *dice* in Command perm get)\n"
				+"*Syntax*: "+prefix+"dice (<number>)";
	}
	@Override
	public String getCommandType() {
		return CMD_TYPE_USER;
	}
}
