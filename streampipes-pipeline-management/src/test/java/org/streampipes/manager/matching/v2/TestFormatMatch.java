package org.streampipes.manager.matching.v2;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.grounding.TransportFormat;

public class TestFormatMatch extends TestCase {

	@Test
	public void testPositiveFormatMatch() {
		
		TransportFormat offeredJson = TestUtils.jsonFormat();
		TransportFormat requiredJson = TestUtils.jsonFormat();
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new FormatMatch().match(offeredJson, requiredJson, errorLog);
		assertTrue(matches);
	}
	
	@Test
	public void testNegativeFormatMatch() {
		
		TransportFormat offeredJson = TestUtils.jsonFormat();
		TransportFormat requiredThrift = TestUtils.thriftFormat();
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new FormatMatch().match(offeredJson, requiredThrift, errorLog);
		assertFalse(matches);
	}
}
