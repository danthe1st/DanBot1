package util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ListWrapper<E> {
	private List<E> data;
	public ListWrapper() {
		setData(new ArrayList<>());
	}
	public ListWrapper(List<E> data) {
		this.setData(data);
	}
	public List<E> getData() {
		return data;
	}
	public void setData(List<E> data) {
		this.data = data;
	}
}
