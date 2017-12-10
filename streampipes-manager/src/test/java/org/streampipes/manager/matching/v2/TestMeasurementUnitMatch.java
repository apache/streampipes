package org.streampipes.manager.matching.v2;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.units.UnitProvider;

public class TestMeasurementUnitMatch extends TestCase {

	@Test
	public void testPositiveMeasurementUnitMatch() {
		
		URI offered = UnitProvider.INSTANCE.getAvailableUnits().get(0).getResource();
		URI required = UnitProvider.INSTANCE.getAvailableUnits().get(0).getResource();
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new MeasurementUnitMatch().match(offered, required, errorLog);
		assertTrue(matches);
	}
	
/*	@consul
	public void testNegativeMeasurementUnitMatch() {
		
		URI offered = UnitProvider.INSTANCE.getAvailableUnits().get(0).getResource();
		URI required = UnitProvider.INSTANCE.getAvailableUnits().get(1).getResource();
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new MeasurementUnitMatch().match(offered, required, errorLog);
		assertFalse(matches);
	}
*/
}
