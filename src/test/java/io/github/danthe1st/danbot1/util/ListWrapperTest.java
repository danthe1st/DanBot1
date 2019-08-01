package io.github.danthe1st.danbot1.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ListWrapperTest {
	@Test
	void testGetData() {
		List<String> list=new ArrayList<>();
		list.add("Hello");
		ListWrapper<String> wrapper=new ListWrapper<>(list);
		assertEquals(list,wrapper.getData());
	}

	@Test
	void testSetData() {
		List<String> list=new ArrayList<>();
		list.add("World");
		ListWrapper<String> wrapper=new ListWrapper<>();
		wrapper.setData(list);
		assertEquals(list,wrapper.getData());
	}

}
