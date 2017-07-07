package de.fzi.cep.sepa.manager.matching.v2;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import de.fzi.cep.sepa.model.client.matching.MatchingResultMessage;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;

public class TestDatatypeMatch extends TestCase {

	@Test
	public void testPositiveDatatypeMatch() {

		String offer = XSD._integer.toString();
		String requirement = XSD._integer.toString();
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new DatatypeMatch().match(offer, requirement, errorLog);
		assertTrue(matches);
	}
	
	@Test
	public void testNegativeDatatypeMatch() {

		String offer = XSD._integer.toString();
		String requirement = XSD._string.toString();
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new DatatypeMatch().match(offer, requirement, errorLog);
		assertFalse(matches);
	}
	
	@Test
	public void testSubPropertyMatch() {

		String offer = XSD._integer.toString();
		String requirement = SO.Number;
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new DatatypeMatch().match(offer, requirement, errorLog);
		assertTrue(matches);
	}
	
	
}

