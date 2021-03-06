package io.github.danthe1st.danbot1.util;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML-Wrapper for Maps
 * @author Daniel Schmid
 * @param <K> The Generic of the Key
 * @param <V> The Generic of the Value
 */
@XmlRootElement
public class MapWrapper<K,V> {
	private Map<K, V> data;
	public MapWrapper() {
		setData(new HashMap<>());
	}
	public MapWrapper(Map<K, V> data) {
		this.setData(data);
	}
	public Map<K, V> getData() {
		return data;
	}
	public void setData(Map<K, V> data) {
		this.data = data;
	}
}
