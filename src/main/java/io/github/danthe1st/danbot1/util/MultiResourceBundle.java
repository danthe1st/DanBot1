package io.github.danthe1st.danbot1.util;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class MultiResourceBundle extends ResourceBundle {

    protected static final Control CONTROL = new MultiResourceBundleControl();
    private Properties properties;

    public MultiResourceBundle(String baseName,Locale locale,ClassLoader loader) {
        //setParent(ResourceBundle.getBundle(baseName,locale,loader, CONTROL));
    	try {
			setParent(CONTROL.newBundle(baseName, locale, "java.properties", loader, false));
		} catch (IllegalAccessException | InstantiationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    protected MultiResourceBundle(Properties properties) {
        this.properties = properties;
    }

    @Override
    protected Object handleGetObject(String key) {
        return properties != null ? properties.get(key) : parent.getObject(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Enumeration<String> getKeys() {
        return properties != null ? (Enumeration<String>) properties.propertyNames() : parent.getKeys();
    }

    protected static class MultiResourceBundleControl extends Control {
        @Override
        public ResourceBundle newBundle(
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
            return new MultiResourceBundle(properties);
        }
        private Properties load(String baseName, ClassLoader loader,Locale locale) throws IOException {
        	Properties props=load(baseName, loader);
        	if (locale!=Locale.getDefault()) {
				load(props, baseName,loader,Locale.getDefault());
			}
        	load(props, baseName,loader,locale);
        	return props;
        }
        private Properties load(Properties props,String baseName, ClassLoader loader,Locale locale) throws IOException {
        	baseName=load(props, baseName, locale.getLanguage(), loader);
        	baseName=load(props, baseName, locale.getCountry(), loader);
        	load(props, baseName, locale.getVariant(), loader);
        	return props;
        }
        private String load(Properties props,String old,String toAdd,ClassLoader loader) throws IOException {
        	String newStr=old+"_"+toAdd;
        	props.putAll(load(newStr, loader));
        	return newStr;
        }
        private Properties load(String baseName, ClassLoader loader) throws IOException {
            Properties properties = new Properties();
            Enumeration<URL> resources=loader.getResources(baseName.replace('.', '/')+".properties");
            while (resources.hasMoreElements()) {
				URL url = (URL) resources.nextElement();
				properties.load(url.openStream());
			}
            
            return properties;
        }
    }

}