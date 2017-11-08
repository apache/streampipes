package org.streampipes.manager.matching.v2;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.vocabulary.Geo;

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
