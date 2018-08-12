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
//	public ListWrapper(ArrayList<E> data) {
//		this.setData(data);
//	}
	
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
