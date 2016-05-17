package de.fzi.proasense.demonstrator.adapter;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilsValueTest {
	@Test
	public void testHexToDouble() {

		assertEquals(1291.1245, UtilsValue.hexToDouble("44a163fc"), 0.0001);
		assertEquals(Float.MIN_VALUE, UtilsValue.hexToDouble("11uz"), 0.0001);
		assertEquals(Float.MIN_VALUE, UtilsValue.hexToDouble("null"), 0.0001);
		assertEquals(Float.MIN_VALUE, UtilsValue.hexToDouble(""), 0.0001);

		assertEquals(10.332, UtilsValue.hexToDouble("41254fdf"), 0.0001);

		assertEquals(-10.332, UtilsValue.hexToDouble("c1254fdf"), 0.0001);

		assertEquals(-0.0004730, UtilsValue.hexToDouble("b9f80000"), 0.0001);


	}
	
	
	@Test
	public void testFourByteString() {
		String uri = "123456789";
		assertEquals("12345678", UtilsValue.getByteString(uri, 0, 8));
		assertEquals("23456789", UtilsValue.getByteString(uri, 1, 8));
		assertEquals("", UtilsValue.getByteString(uri, 2, 8));
	}

}
