package io.github.danthe1st.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML-Wrapper Class for Lists
 * @author Daniel Schmid
 * @param <E> the Generic of the List Elements
 */
@XmlRootElement
public class ListWrapper<E> {
	private List<E> data;
	public ListWrapper() {
		setData(new ArrayList<>());
	}
	public ListWrapper(List<E> data) {
		this.data=new ArrayList<>(data);
	}
	public List<E> getData() {
		return data;
	}
	public void setData(List<E> data) {
		this.data = data;
	}
}
