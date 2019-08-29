package io.github.danthe1st.danbot1.util;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public class PropertiesResourceBundle extends ResourceBundle {

    private Properties properties;

    protected PropertiesResourceBundle(Properties properties) {
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
}