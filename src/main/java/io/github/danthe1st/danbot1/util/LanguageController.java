package io.github.danthe1st.danbot1.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.dv8tion.jda.api.entities.Guild;

/**
 * Class for Language Packages
 * @author Daniel Schmid
 */
public class LanguageController {
	private static final String baseName="languages.DanBot1";
	private static final ResourceBundle DEFAULT_BUNDLE=ResourceBundle.getBundle(baseName,Locale.ENGLISH);
	private static Map<Guild, ResourceBundle> bundles=new HashMap<>();
	private static ResourceBundle getResourceBundle(Guild g) {
		if (g!=null&&bundles.containsKey(g)) {
			return bundles.get(g);
		}else {
			return DEFAULT_BUNDLE;
		}
	}
	public static Locale getLocale(Guild g) {
		return getResourceBundle(g).getLocale();
	}
	public static void setLocale(Guild g,Locale locale) {//TODO command for editing Locale per Guild
		bundles.put(g, ResourceBundle.getBundle(baseName,locale));
	}
	public static String translate(Guild g,String s) {
		try {
			return getResourceBundle(g).getString(s);
		}catch (MissingResourceException e) {
			return s;
		}
	}
}
