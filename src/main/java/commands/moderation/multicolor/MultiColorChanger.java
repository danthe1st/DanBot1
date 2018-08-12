package commands.moderation.multicolor;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.exceptions.PermissionException;
import util.ListWrapper;
import util.MapWrapper;
import util.STATIC;
/**
 * Core Command for multicolor System
 * @author Daniel Schmid
 */
public final class MultiColorChanger implements Runnable{

	private List<Role> roles=new ArrayList<>();
	private Color[] colers= {
			Color.red,Color.green,Color.yellow
			
	};
	private static MultiColorChanger changer=new MultiColorChanger();
	private MultiColorChanger() {
		Thread changerThread= new Thread(this);
		changerThread.setDaemon(true);
		changerThread.start();
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
		if (!changer.roles.contains(role)) {
			changer.roles.add(role);
		}
		saveRoles();
	}
	public static void removeMultiColorRole(Role role) {
		changer.roles.remove(role);
		saveRoles();
	}
	public static List<Role> getMultiColorRoles(Guild g){
		List<Role> roles=new ArrayList<>();
		for (Role role : changer.roles) {
			if (g.getIdLong()==role.getGuild().getIdLong()) {
				roles.add(role);
			}
		}
		return roles;
	}
	public static void loadRoles(JDA jda) {
		try {
			final File file=new File(STATIC.getSettingsDir()+"/multicolorRoles.xml");
			JAXBContext context=JAXBContext.newInstance(MapWrapper.class);
			 Unmarshaller um = context.createUnmarshaller();

		        // Reading XML from the file and unmarshalling.
			 @SuppressWarnings("unchecked")
			 ListWrapper<Long> data = (ListWrapper<Long>) um.unmarshal(file);
		       List<Long> ids=data.getData();
			 
		       if (ids==null) {
					return;
				}
				changer.roles.clear();
				for (Long id : ids) {
					changer.roles.add(jda.getRoleById(id.longValue()));
				}
		} catch (JAXBException e) {
		}
		
		
//		@SuppressWarnings("unchecked")
//		List<Long> ids=(List<Long>) STATIC.load("multiColorRoles.dat");
//		if (ids==null) {
//			return;
//		}
//		changer.roles.clear();
//		for (Long id : ids) {
//			changer.roles.add(jda.getRoleById(id.longValue()));
//		}
	}
	public static void saveRoles() {
		List<Long> ids=new ArrayList<>();
		for (Role role : changer.roles) {
			ids.add(Long.valueOf(role.getIdLong()));
		}
		
		
		File file=new File(STATIC.getSettingsDir()+"/multicolorRoles.xml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		}
		try {
			JAXBContext context = JAXBContext
			        .newInstance(ListWrapper.class);
			Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        m.marshal(new ListWrapper<Long>(ids), file);
		} catch (JAXBException e) {
			
		}
		
		//STATIC.save("multiColorRoles.dat", ids);
	}
}
