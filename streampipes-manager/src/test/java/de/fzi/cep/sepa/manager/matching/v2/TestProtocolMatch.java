package de.fzi.cep.sepa.manager.matching.v2;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import de.fzi.cep.sepa.model.client.matching.MatchingResultMessage;
import de.fzi.cep.sepa.model.impl.TransportProtocol;

public class TestProtocolMatch extends TestCase {
	
	@Test
	public void testPositiveProtocolMatch() {
		
		TransportProtocol offer = TestUtils.kafkaProtocol();
		TransportProtocol requirement = TestUtils.kafkaProtocol();
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new ProtocolMatch().match(offer, requirement, errorLog);
		assertTrue(matches);
	}
	
	@Test
	public void testNegativeProtocolMatch() {
		
		TransportProtocol offer = TestUtils.kafkaProtocol();
		TransportProtocol requirement = TestUtils.jmsProtocol();
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new ProtocolMatch().match(offer, requirement, errorLog);
		assertFalse(matches);
	}
}
