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
public class LanguageController {//TODO fix with Plugins
	private static final String baseName="languages.DanBot1";
	private static final ResourceBundle DEFAULT_BUNDLE=ResourceBundle.getBundle(baseName,Locale.getDefault(),LanguageController.class.getClassLoader());
	private static Map<Guild, ResourceBundle> bundles=new HashMap<>();
	static {
		Locale.setDefault(Locale.ENGLISH);
	}
	public static ResourceBundle getResourceBundle(Guild g) {
		if (g!=null&&bundles.containsKey(g)) {
			return bundles.get(g);
		}else {
			return DEFAULT_BUNDLE;
		}
	}
	public static Locale getLocale(Guild g) {
		return getResourceBundle(g).getLocale();
	}
	public static void setLocale(Guild g,Locale locale) {
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
