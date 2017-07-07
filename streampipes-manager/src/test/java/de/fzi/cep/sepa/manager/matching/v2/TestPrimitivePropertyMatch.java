package de.fzi.cep.sepa.manager.matching.v2;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import de.fzi.cep.sepa.model.client.matching.MatchingResultMessage;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.vocabulary.Geo;

public class TestPrimitivePropertyMatch extends TestCase {

	@Test
	public void testPositivePrimitivePropertyMatch() {

		EventPropertyPrimitive offer = EpProperties.integerEp("timestamp", Geo.lat);
		EventPropertyPrimitive requirement = EpRequirements.integerReq();
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new PropertyMatch().match(offer, requirement, errorLog);
		assertTrue(matches);
	}
	
	@Test
	public void testNegativePrimitivePropertyMatch() {

		EventPropertyPrimitive offer = EpProperties.integerEp("timestamp", Geo.lat);
		EventPropertyPrimitive requirement = EpRequirements.stringReq();
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new PropertyMatch().match(offer, requirement, errorLog);
		assertFalse(matches);
	}
	
	@Test
	public void testNegativePrimitivePropertyMatchDomain() {

		EventPropertyPrimitive offer = EpProperties.integerEp("timestamp", Geo.lat);
		EventPropertyPrimitive requirement = EpRequirements.domainPropertyReq(Geo.lng); 
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new PropertyMatch().match(offer, requirement, errorLog);
		assertFalse(matches);
	}
}
