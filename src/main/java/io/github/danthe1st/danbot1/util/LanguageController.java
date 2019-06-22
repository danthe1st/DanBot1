package io.github.danthe1st.danbot1.util;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

/**
 * Class for Language Packages
 * @author Daniel Schmid
 */
public class LanguageController {
	private static final String BASE_NAME="languages.DanBot1";
	private static final ResourceBundle DEFAULT_BUNDLE;
	//private static Map<Guild, ResourceBundle> bundles=new HashMap<>();
	private static Map<Guild, Locale> locales=new HashMap<>();
	private static Map<Locale, ResourceBundle> localePluginBundles=new HashMap<>();
	private static ClassLoader pluginLoader=null;
	private static ClassLoader coreLoader=LanguageController.class.getClassLoader();
	static {
		Locale.setDefault(Locale.ENGLISH);
		DEFAULT_BUNDLE=ResourceBundle.getBundle(BASE_NAME,Locale.getDefault(),coreLoader);
	}
	private LanguageController() {
		//do not allow
		throw new InstantiationError();
	}
	public static void setPluginLoader(ClassLoader pluginLoader) {
		LanguageController.pluginLoader=pluginLoader;
	}
	private static ResourceBundle getGlobalResourceBundle(Guild g) {
		Locale locale=getLocale(g);
		ResourceBundle guildBundle=ResourceBundle.getBundle(BASE_NAME,locale);
		if (guildBundle!=null) {
			return guildBundle;
		}
		/*if (g!=null&&bundles.containsKey(g)) {
			return bundles.get(g);
		}*/else {
			return DEFAULT_BUNDLE;
		}
	}
	private static ResourceBundle getPluginResourceBundle(Locale locale) {
		if (localePluginBundles.containsKey(locale)) {
			return localePluginBundles.get(locale);
		}
		if (pluginLoader!=null) {
			try {
				return newBundle(BASE_NAME, locale, "java.properties", pluginLoader, false);
			} catch (IllegalAccessException | InstantiationException | IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	private static Set<ResourceBundle> getResourceBundles(Guild g){
		Set<ResourceBundle> bundles=new HashSet<>();
		bundles.add(getGlobalResourceBundle(g));
		ResourceBundle pluginBundle=getPluginResourceBundle(getLocale(g));
		if (pluginBundle!=null) {
			bundles.add(pluginBundle);
		}
		return bundles;
	}
	/*private static ResourceBundle getResourceBundle(Guild g,ClassLoader loader) {
		//return ResourceBundle.getBundle(BASE_NAME,getLocale(g),loader);
		//return new PropertiesResourceBundle(BASE_NAME, getLocale(g),loader);
		try {
			return newBundle(BASE_NAME, getLocale(g), "java.properties", loader, false);
		} catch (IllegalAccessException | InstantiationException | IOException e) {
			e.printStackTrace();
		}
		return null;
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
	}*/
	public static Locale getLocale(Guild g) {
		Locale locale=locales.get(g);
		if (locale==null) {
			locale=Locale.ENGLISH;
		}
		return locale;
		//return getGlobalResourceBundle(g).getLocale();
	}
	public static void setLocale(Guild g,Locale locale) {
		locales.put(g, locale);
		//bundles.put(g, ResourceBundle.getBundle(BASE_NAME,locale));
		saveToFile();
	}
	public static String translate(Guild g,String s) {
		for (ResourceBundle bundle : getResourceBundles(g)) {
			try {
				return bundle.getString(s);
			}catch (MissingResourceException e) {
				//ignore/next iteration
			}
		}
		return s;
	}
	
	
	
    private static ResourceBundle newBundle(
        String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException
    {
        Properties properties = load(baseName, loader,locale);
        String include = properties.getProperty("include");
        if (include != null) {
            for (String includeBaseName : include.split("\\s*,\\s*")) {
                properties.putAll(load(includeBaseName, loader));
            }
        }
        return new PropertiesResourceBundle(properties);
    }
    private static Properties load(String baseName, ClassLoader loader,Locale locale) throws IOException {
    	Properties props=load(baseName, loader);
    	if (locale!=Locale.getDefault()) {
			load(props, baseName,loader,Locale.getDefault());
		}
    	load(props, baseName,loader,locale);
    	return props;
    }
    private static Properties load(Properties props,String baseName, ClassLoader loader,Locale locale) throws IOException {
    	baseName=load(props, baseName, locale.getLanguage(), loader);
    	baseName=load(props, baseName, locale.getCountry(), loader);
    	load(props, baseName, locale.getVariant(), loader);
    	return props;
    }
    private static String load(Properties props,String old,String toAdd,ClassLoader loader) throws IOException {
    	String newStr=old+"_"+toAdd;
    	props.putAll(load(newStr, loader));
    	return newStr;
    }
    private static Properties load(String baseName, ClassLoader loader) throws IOException {
        Properties properties = new Properties();
        if (loader==null||baseName==null) {
			return properties;
		}
        Enumeration<URL> resources=loader.getResources(baseName.replace('.', '/')+".properties");
        while (resources.hasMoreElements()) {
			URL url = (URL) resources.nextElement();
			properties.load(url.openStream());
		}
        return properties;
    }
    private static void saveToFile() {
    	Map<String, Locale> locales=new HashMap<>();
    	LanguageController.locales.forEach((k,v)->{
    		locales.put(k.getId(), v);
    	});
    	STATIC.save("/languages.dat", locales);
    }
    public static void load(JDA jda) {
    	@SuppressWarnings("unchecked")
		Map<String, Locale> locales=(Map<String, Locale>) STATIC.load("/languages.dat");
    	if (locales!=null) {
			locales.forEach((k,v)->{
				setLocale(jda.getGuildById(k), v);
			});
		}
    }
}

