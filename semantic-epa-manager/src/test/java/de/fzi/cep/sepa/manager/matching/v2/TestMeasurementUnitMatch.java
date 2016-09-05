package de.fzi.cep.sepa.manager.matching.v2;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import de.fzi.cep.sepa.model.client.matching.MatchingResultMessage;
import de.fzi.cep.sepa.units.UnitProvider;

public class TestMeasurementUnitMatch extends TestCase {

	@Test
	public void testPositiveMeasurementUnitMatch() {
		
		URI offered = UnitProvider.INSTANCE.getAvailableUnits().get(0).getResource();
		URI required = UnitProvider.INSTANCE.getAvailableUnits().get(0).getResource();
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new MeasurementUnitMatch().match(offered, required, errorLog);
		assertTrue(matches);
	}
	
/*	@Test
	public void testNegativeMeasurementUnitMatch() {
		
		URI offered = UnitProvider.INSTANCE.getAvailableUnits().get(0).getResource();
		URI required = UnitProvider.INSTANCE.getAvailableUnits().get(1).getResource();
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new MeasurementUnitMatch().match(offered, required, errorLog);
		assertFalse(matches);
	}
*/
}
