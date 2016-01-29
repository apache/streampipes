package semantic_epa_sources_demonstrator.sources_demonstrator;

import static org.junit.Assert.*;

import org.apache.commons.collections.set.SynchronizedSet;
import org.junit.Test;

public class FlowRateSensorValueTest {

	@Test
	public void testHexToDouble() {
		FlowRateSensorValue v = new FlowRateSensorValue("");

		assertEquals(1291.1245, v.hexToDouble("44a163fc"), 0.0001);
		assertEquals(Float.MIN_VALUE, v.hexToDouble("11uz"), 0.0001);
		assertEquals(Float.MIN_VALUE, v.hexToDouble("null"), 0.0001);
		assertEquals(Float.MIN_VALUE, v.hexToDouble(""), 0.0001);

	}
	
	@Test
	public void testFourByteString() {
		FlowRateSensorValue v = new FlowRateSensorValue("123456789");
		assertEquals("12345678", v.getByteString(0, 8));
		assertEquals("23456789", v.getByteString(1, 8));
		assertEquals("", v.getByteString(2, 8));
	}

	@Test
	public void testFlowSensorValue() {
		//TODO get real values
		FlowRateSensorValue v = new FlowRateSensorValue("0103203900000034094000446ea01a00000001858f507c41d14fdc0000000bff0000006ecc 00");
		System.out.println(v.toJson());
	}

}
