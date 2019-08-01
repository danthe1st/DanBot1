package io.github.danthe1st.danbot1.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class MapWrapperTest {
	@Test
	void testGetData() {
		Map<String,String> map=new HashMap<>();
		map.put("Hello", "World");
		MapWrapper<String,String> wrapper=new MapWrapper<>(map);
		assertEquals(map,wrapper.getData());
	}

	@Test
	void testSetData() {
		Map<String,String> map=new HashMap<>();
		map.put("Hello", "World");
		MapWrapper<String,String> wrapper=new MapWrapper<>();
		wrapper.setData(map);
		assertEquals(map,wrapper.getData());
	}

}
