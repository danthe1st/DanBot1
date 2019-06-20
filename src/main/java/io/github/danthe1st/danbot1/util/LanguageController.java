package io.github.danthe1st.danbot1.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import net.dv8tion.jda.api.entities.Guild;

/**
 * Class for Language Packages
 * @author Daniel Schmid
 */
public class LanguageController {//TODO fix with Plugins
	private static final String BASE_NAME="languages.DanBot1";
	private static final ResourceBundle DEFAULT_BUNDLE=ResourceBundle.getBundle(BASE_NAME,Locale.getDefault(),LanguageController.class.getClassLoader());
	private static Map<Guild, ResourceBundle> bundles=new HashMap<>();
	private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
	static {
		Locale.setDefault(Locale.ENGLISH);
	}
	private static ResourceBundle getGlobalResourceBundle(Guild g) {
		if (g!=null&&bundles.containsKey(g)) {
			return bundles.get(g);
		}else {
			return DEFAULT_BUNDLE;
		}
	}
	private static ResourceBundle getResourceBundle(Guild g,ClassLoader loader) {
		//return ResourceBundle.getBundle(BASE_NAME,getLocale(g),loader);
		return new MultiResourceBundle(BASE_NAME, getLocale(g),loader);
	}
	private static Set<ResourceBundle> getResourceBundles(Guild g,ClassLoader... loaders){
		Set<ResourceBundle> bundles=new HashSet<>();
		bundles.add(getGlobalResourceBundle(g));
		for (ClassLoader loader : loaders) {
			
			ResourceBundle pluginBundle=getResourceBundle(g,loader);
			if (pluginBundle!=null) {
				bundles.add(pluginBundle);
			}
		}
		return bundles;
	}
	public static Locale getLocale(Guild g) {
		return getGlobalResourceBundle(g).getLocale();
	}
	public static void setLocale(Guild g,Locale locale) {
		bundles.put(g, ResourceBundle.getBundle(BASE_NAME,locale));
	}
	public static String translate(Guild g,String s) {
		for (ResourceBundle bundle : getResourceBundles(g,STACK_WALKER.getCallerClass().getClassLoader())) {
			try {
				return bundle.getString(s);
			}catch (MissingResourceException e) {
				//ignore/next iteration
			}
		}
		return s;
	}
}
