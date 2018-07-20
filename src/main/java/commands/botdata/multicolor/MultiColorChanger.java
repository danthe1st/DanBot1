package commands.botdata.multicolor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.exceptions.PermissionException;
import util.STATIC;
/**
 * Klasse für änderung der Farben der Multicolor-Rollen
 * @author Daniel Schmid
 *
 */
public final class MultiColorChanger implements Runnable{

	private List<Role> roles=new ArrayList<>();
	private Color[] colers= {
			Color.red,Color.green,Color.yellow
			
	};
	private static MultiColorChanger changer=new MultiColorChanger();
	
	private MultiColorChanger() {
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		while (true) {
			for (int i = 0; i < colers.length; i++) {
				try {
					for (Role role : roles) {
						try {
							role.getManager().setColor(colers[i]).complete();
						} catch (PermissionException e) {
							roles.remove(role);
						}
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				} catch (Exception e) {
				}
				
			}
		}
	}
	public static void addMultiColorRole(Role role) {
		changer.roles.add(role);
		
		saveRoles();
	}
	public static void removeMultiColorRole(Role role) {
		changer.roles.remove(role);
		saveRoles();
	}
	public static List<Role> getMultiColorRoles(Guild g) {
		List<Role> roles=new ArrayList<>();
		for (Role role : changer.roles) {
			if (role.getGuild().getIdLong()==g.getIdLong()) {
				roles.add(role);
			}
		}
		return roles;
	}
	public static void loadRoles(JDA jda) {
		@SuppressWarnings("unchecked")
		List<Long> ids=(List<Long>) STATIC.load("multiColorRoles.dat");
		if (ids==null) {
			return;
		}
		changer.roles.clear();
		for (Long id : ids) {
			changer.roles.add(jda.getRoleById(id.longValue()));
		}
	}
	public static void saveRoles() {
		List<Long> ids=new ArrayList<>();
		for (Role role : changer.roles) {
			ids.add(Long.valueOf(role.getIdLong()));
		}
		STATIC.save("multiColorRoles.dat", ids);
	}
}
