package org.streampipes.manager.matching.v2;

import junit.framework.TestCase;
import org.junit.Test;
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.vocabulary.Geo;

import java.util.ArrayList;
import java.util.List;

public class TestPrimitivePropertyMatch extends TestCase {

	@Test
	public void testPositivePrimitivePropertyMatch() {

		EventPropertyPrimitive offer = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.lat);
		EventPropertyPrimitive requirement = EpRequirements.integerReq();
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new PropertyMatch().match(offer, requirement, errorLog);
		assertTrue(matches);
	}
	
	@Test
	public void testNegativePrimitivePropertyMatch() {

		EventPropertyPrimitive offer = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.lat);
		EventPropertyPrimitive requirement = EpRequirements.stringReq();
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new PropertyMatch().match(offer, requirement, errorLog);
		assertFalse(matches);
	}
	
	@Test
	public void testNegativePrimitivePropertyMatchDomain() {

		EventPropertyPrimitive offer = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.lat);
		EventPropertyPrimitive requirement = EpRequirements.domainPropertyReq(Geo.lng); 
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new PropertyMatch().match(offer, requirement, errorLog);
		assertFalse(matches);
	}
}
